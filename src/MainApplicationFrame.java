// MainApplicationFrame.java
// This is the main window of the application.

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

    // Inner class for the about dialog
    private class AboutDialog extends JDialog {
        public AboutDialog(JFrame owner) {
            super(owner, "About Smart Revision Organizer", true);
            setSize(450, 450);
            setLocationRelativeTo(owner);
            setResizable(false);

            JEditorPane editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");

            try {
                URL htmlUrl = getClass().getResource("/about/about_app.html");
                if (htmlUrl != null) {
                    editorPane.setPage(htmlUrl);
                } else {
                    editorPane.setText("<html><body><h1>About Smart Revision Organizer</h1><p>A simple, yet effective tool for organizing your learning and applying spaced repetition to maximize recall. Designed and built by Irin Sultana & Sujana Farid.</p></body></html>");
                }
            } catch (IOException e) {
                editorPane.setText("<html><body><h1>Error loading content!</h1><p>" + e.getMessage() + "</p></body></html>");
                e.printStackTrace();
            }

            JScrollPane scrollPane = new JScrollPane(editorPane);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(closeButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    // Inner class for the theory dialog
    private class TheoryDialog extends JDialog {
        public TheoryDialog(JFrame owner) {
            super(owner, "Spaced Repetition Theory", true);
            setSize(600, 500);
            setLocationRelativeTo(owner);

            JEditorPane editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");

            try {
                URL htmlUrl = getClass().getResource("/theory/sm2_theory.html");
                if (htmlUrl != null) {
                    editorPane.setPage(htmlUrl);
                } else {
                    editorPane.setText("<html><body><h1>SuperMemo 2 (SM-2) Algorithm</h1><p>The SM-2 algorithm is a key component of spaced repetition systems. It calculates the optimal time to review an item based on your previous recall performance.</p></body></html>");
                }
            } catch (IOException e) {
                editorPane.setText("<html><body><h1>Error loading content!</h1><p>" + e.getMessage() + "</p></body></html>");
                e.printStackTrace();
            }

            JScrollPane scrollPane = new JScrollPane(editorPane);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(closeButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

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

        setupMenuBar();

        mainContentPanel = new JPanel();
        cardLayout = new CardLayout();
        mainContentPanel.setLayout(cardLayout);

        sidebarPanel = new SidebarPanel(dataManager, this);
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBackground(new Color(64, 46, 75)); // Dark purple from design

        DashboardPanel dashboardPanel = new DashboardPanel(dataManager, this);
        mainContentPanel.add(dashboardPanel, "Dashboard");

        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        loadDataInBackground();
    }

    private void setupMenuBar() {
        menuBar = new JMenuBar();
        helpMenu = new JMenu("Help");
        aboutItem = new JMenuItem("About Smart Revision Organizer...");
        theoryItem = new JMenuItem("Spaced Repetition Theory");

        aboutItem.addActionListener(e -> {
            AboutDialog dialog = new AboutDialog(this);
            dialog.setVisible(true);
        });

        theoryItem.addActionListener(e -> {
            TheoryDialog dialog = new TheoryDialog(this);
            dialog.setVisible(true);
        });

        helpMenu.add(theoryItem);
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    public void showPanel(String name) {
        cardLayout.show(mainContentPanel, name);
    }

    public void showCourseDetail(UUID courseId) {
        String cardName = "CourseDetail_" + courseId.toString();
        boolean panelExists = false;
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                panelExists = true;
                break;
            }
        }

        if (!panelExists) {
            CourseDetailPanel detailPanel = new CourseDetailPanel(dataManager, courseId, this);
            detailPanel.setName(cardName);
            mainContentPanel.add(detailPanel, cardName);
        }

        showPanel(cardName);
    }

    public void showTopicDetail(UUID courseId, UUID topicId) {
        String cardName = "TopicDetail_" + topicId.toString();
        boolean panelExists = false;
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                panelExists = true;
                break;
            }
        }

        if (!panelExists) {
            TopicDetailPanel detailPanel = new TopicDetailPanel(dataManager, courseId, topicId, this);
            detailPanel.setName(cardName);
            mainContentPanel.add(detailPanel, cardName);
        }

        showPanel(cardName);
    }

    public void showResourceReview(UUID resourceId) {
        String cardName = "ResourceReview_" + resourceId.toString();
        boolean panelExists = false;
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                panelExists = true;
                break;
            }
        }

        if (!panelExists) {
            ResourceReviewPanel reviewPanel = new ResourceReviewPanel(dataManager, resourceId, this);
            reviewPanel.setName(cardName);
            mainContentPanel.add(reviewPanel, cardName);
        }

        showPanel(cardName);
    }

    public void showDashboard() {
        cardLayout.show(mainContentPanel, "Dashboard");
    }

    public void updateSidebar() {
        sidebarPanel.refreshCourseList();
    }

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
                JOptionPane.showMessageDialog(MainApplicationFrame.this, "Data loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        };

        worker.execute();
    }

    public void saveDataInBackground() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
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
}
