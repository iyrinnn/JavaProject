import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class MainApplicationFrame extends JFrame {
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private DataManager dataManager;
    private SidebarPanel sidebarPanel;
    private JMenuBar menuBar;
    private JMenu helpMenu;
    private JMenuItem aboutItem;
    private JMenuItem theoryItem;
    private String currentUsername;

    // Inner classes AboutDialog and TheoryDialog (unchanged)
    private class AboutDialog extends JDialog {
        public AboutDialog(JFrame owner) {
            super(owner, "About Smart Revision & Resource Organizer", true);
            setSize(400, 200);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(10, 10));
            add(new JLabel("<html><h2 style='text-align: center;'>Smart Revision & Resource Organizer</h2>" +
                    "<p style='text-align: center;'>Version 1.0</p>" +
                    "<p style='text-align: center;'>Developed by Irin and Sujana </p></html>", SwingConstants.CENTER), BorderLayout.CENTER);
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private class TheoryDialog extends JDialog {
        public TheoryDialog(JFrame owner) {
            super(owner, "Spaced Repetition Theory", true);
            setSize(600, 400);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(10, 10));

            JTextArea theoryText = new JTextArea();
            theoryText.setEditable(false);
            theoryText.setLineWrap(true);
            theoryText.setWrapStyleWord(true);
            theoryText.setText(
                    "Spaced repetition is an evidence-based learning technique that is usually performed with flashcards. " +
                            "Newly introduced and more difficult flashcards are shown more frequently, while older and " +
                            "less difficult flashcards are shown less frequently. The time interval between successive " +
                            "reviews of a given flashcard gradually increases.\n\n" +
                            "This application uses a simplified spaced repetition algorithm. " +
                            "The next review date is calculated based on your recall rating:\n" +
                            "0 (Blackout): 1 day\n" +
                            "1 (Very Poor): 2 days\n" +
                            "2 (Moderate): 4 days\n" +
                            "3 (Good): 8 days\n" +
                            "4 (Perfect): 16 days\n\n" +
                            "Consistent review is key to long-term retention!"
            );
            JScrollPane scrollPane = new JScrollPane(theoryText);
            add(scrollPane, BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    // Updated constructor to accept username
    public MainApplicationFrame(DataManager dataManager, String username) {
        this.dataManager = dataManager;
        this.currentUsername = username;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Smart Revision & Resource Organizer - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        mainContentPanel = new JPanel();
        cardLayout = new CardLayout();
        mainContentPanel.setLayout(cardLayout);

        sidebarPanel = new SidebarPanel(dataManager, this);
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBackground(new Color(0xF7F6F3));

        DashboardPanel dashboardPanel = new DashboardPanel(dataManager, this);
        mainContentPanel.add(dashboardPanel, "Dashboard");

        setLayout(new BorderLayout());

        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        createMenuBar(); // Call to create the menu bar
        setJMenuBar(menuBar); // Set the menu bar to the frame

        // Remove the automatic data loading since we're loading user data before creating this frame
        // Just refresh the UI with the already loaded data
        updateSidebar();
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp instanceof DashboardPanel) {
                ((DashboardPanel) comp).refreshDashboard();
                break;
            }
        }
    }

    // Keep the old constructor for backward compatibility (if needed)
    public MainApplicationFrame(DataManager dataManager) {
        this(dataManager, "Guest"); // Default to "Guest" if no username provided
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();
        helpMenu = new JMenu("Help");
        aboutItem = new JMenuItem("About");
        theoryItem = new JMenuItem("Spaced Repetition Theory");

        aboutItem.addActionListener(e -> new AboutDialog(this).setVisible(true));
        theoryItem.addActionListener(e -> new TheoryDialog(this).setVisible(true));

        helpMenu.add(aboutItem);
        helpMenu.add(theoryItem);
        menuBar.add(helpMenu);
    }

    public void showPanel(String name) {
        System.out.println("MainApplicationFrame: Attempting to show panel: " + name); // Debug print
        cardLayout.show(mainContentPanel, name);
    }

    public void showCourseDetail(UUID courseId) {
        String cardName = "CourseDetail_" + courseId;
        CourseDetailPanel detailPanel = getCourseDetailPanel(courseId); // Try to get existing panel

        if (detailPanel == null) { // If not found, create new
            Course course = dataManager.getCourseById(courseId);
            detailPanel = new CourseDetailPanel(this, course, dataManager);
            detailPanel.setName(cardName);
            mainContentPanel.add(detailPanel, cardName);
            System.out.println("MainApplicationFrame: Created new CourseDetailPanel for course ID: " + courseId);
        } else {
            detailPanel.refreshPanel(); // Refresh existing panel
            System.out.println("MainApplicationFrame: Refreshed existing CourseDetailPanel for course ID: " + courseId);
        }
        showPanel(cardName);
    }

    public void showTopicDetail(UUID courseId, UUID topicId) {
        String cardName = "TopicDetail_" + topicId;
        System.out.println("Attempting to show topic detail: " + cardName);

        // Remove existing panel with same name to avoid duplicates
        for (Component comp : mainContentPanel.getComponents()) {
            if (cardName.equals(comp.getName())) {
                mainContentPanel.remove(comp);
                break;
            }
        }

        // Create new panel
        TopicDetailPanel detailPanel = new TopicDetailPanel(dataManager, courseId, topicId, this);
        detailPanel.setName(cardName);
        mainContentPanel.add(detailPanel, cardName);

        // Show the panel
        cardLayout.show(mainContentPanel, cardName);
        System.out.println("Showing topic detail panel: " + cardName);

        // Force UI update
        revalidate();
        repaint();
    }

    public void showTopicReview(UUID topicId) {
        String cardName = "TopicReview_" + topicId;
        // Always create a new review panel to ensure fresh state, or ensure it refreshes properly
        // For simplicity, let's just create a new one each time for review sessions.
        // If the user navigates back and forth frequently, consider caching and refreshing.
        TopicReviewPanel reviewPanel = new TopicReviewPanel(dataManager, topicId, this);
        reviewPanel.setName(cardName);
        // Remove existing panel if it exists to avoid adding duplicates to CardLayout
        for (Component comp : mainContentPanel.getComponents()) {
            if (cardName.equals(comp.getName())) {
                mainContentPanel.remove(comp);
                break;
            }
        }
        mainContentPanel.add(reviewPanel, cardName);
        System.out.println("MainApplicationFrame: Created new TopicReviewPanel for topic ID: " + topicId);
        showPanel(cardName);
    }

    public void showDashboard() {
        // Ensure dashboard is refreshed when shown
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp instanceof DashboardPanel) {
                ((DashboardPanel) comp).refreshDashboard();
                break;
            }
        }
        showPanel("Dashboard");
    }

    public void updateSidebar() {
        sidebarPanel.refreshCourseList();
    }

    // Modified to save user-specific data
    public void saveDataInBackground() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Save to user-specific file
                UserManager.saveUserData(currentUsername, dataManager);
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                System.out.println("Data saved successfully for user: " + currentUsername);
            }
        };
        worker.execute();
    }

    // Optional: Keep the old loadDataInBackground for backward compatibility if needed
    private void loadDataInBackground() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                dataManager.loadData();
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                updateSidebar();
                // Refresh dashboard after data load
                for (Component comp : mainContentPanel.getComponents()) {
                    if (comp instanceof DashboardPanel) {
                        ((DashboardPanel) comp).refreshDashboard();
                        break;
                    }
                }
                JOptionPane.showMessageDialog(MainApplicationFrame.this, "Data loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }

    // Helper method to get an existing CourseDetailPanel instance
    public CourseDetailPanel getCourseDetailPanel(UUID courseId) {
        String cardName = "CourseDetail_" + courseId;
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp instanceof CourseDetailPanel && cardName.equals(comp.getName())) {
                return (CourseDetailPanel) comp;
            }
        }
        return null;
    }

    // Helper method to get an existing TopicDetailPanel instance
    public TopicDetailPanel getTopicDetailPanel(UUID topicId) {
        String cardName = "TopicDetail_" + topicId;
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp instanceof TopicDetailPanel && cardName.equals(comp.getName())) {
                return (TopicDetailPanel) comp;
            }
        }
        return null;
    }

    public void deleteCourse(UUID courseId) {
        dataManager.deleteCourse(courseId);
        saveDataInBackground();
        showDashboard();
        updateSidebar();
    }


    public String getCurrentUsername() {
        return currentUsername;
    }
}