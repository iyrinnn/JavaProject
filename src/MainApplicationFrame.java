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

    // Inner classes AboutDialog and TheoryDialog as you wrote them (unchanged) ...
    // (I omitted them here for brevity, but you keep them unchanged in your code)

    public MainApplicationFrame(DataManager dataManager) {
        this.dataManager = dataManager;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Smart Revision & Resource Organizer");
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

        // **Here is the fix: Set the layout manager of the JFrame to BorderLayout first**
        setLayout(new BorderLayout());

        // Add sidebar to WEST and main content to CENTER (correct BorderLayout positions)
        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        loadDataInBackground();
    }

    // All other methods unchanged...

    public void showPanel(String name) {
        cardLayout.show(mainContentPanel, name);
    }

    public void showCourseDetail(UUID courseId) {
        String cardName = "CourseDetail_" + courseId;
        if (!isPanelLoaded(cardName)) {
            Course course = dataManager.getCourseById(courseId);
            CourseDetailPanel detailPanel = new CourseDetailPanel(this, course, dataManager);

            detailPanel.setName(cardName);
            mainContentPanel.add(detailPanel, cardName);
        }
        showPanel(cardName);
    }

    public void showTopicDetail(UUID courseId, UUID topicId) {
        String cardName = "TopicDetail_" + topicId;
        if (!isPanelLoaded(cardName)) {
            TopicDetailPanel detailPanel = new TopicDetailPanel(dataManager, courseId, topicId, this);
            detailPanel.setName(cardName);
            mainContentPanel.add(detailPanel, cardName);
        }
        showPanel(cardName);
    }

    public void showTopicReview(UUID topicId) {
        String cardName = "TopicReview_" + topicId;
        if (!isPanelLoaded(cardName)) {
            TopicReviewPanel reviewPanel = new TopicReviewPanel(dataManager, topicId, this);
            reviewPanel.setName(cardName);
            mainContentPanel.add(reviewPanel, cardName);
        }
        showPanel(cardName);
    }

    public void showDashboard() {
        showPanel("Dashboard");
    }

    public void updateSidebar() {
        sidebarPanel.refreshCourseList();
    }

    private void loadDataInBackground() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                dataManager.loadData();
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                updateSidebar();
                JOptionPane.showMessageDialog(MainApplicationFrame.this, "Data loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }

    public void saveDataInBackground() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                dataManager.saveData();
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                System.out.println("Data saved successfully.");
            }
        };
        worker.execute();
    }

    private boolean isPanelLoaded(String cardName) {
        for (Component comp : mainContentPanel.getComponents()) {
            if (cardName.equals(comp.getName())) {
                return true;
            }
        }
        return false;
    }

    public void deleteCourse(UUID courseId) {
        dataManager.deleteCourse(courseId);
        saveDataInBackground();
        showDashboard();
        updateSidebar();
    }
}
