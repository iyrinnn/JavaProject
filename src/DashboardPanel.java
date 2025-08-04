// DashboardPanel.java
// This panel shows the dashboard with upcoming reviews.

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DashboardPanel extends JPanel {
    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private DefaultListModel<Resource> upcomingReviewsListModel;
    private ActivityChartDemo activityChartDemo = new ActivityChartDemo();

    public DashboardPanel(DataManager dataManager, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        JLabel welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel upcomingReviewsPanel = new JPanel(new BorderLayout());
        upcomingReviewsPanel.setBorder(BorderFactory.createTitledBorder("Upcoming Reviews"));
        upcomingReviewsPanel.setBackground(Color.WHITE);

        upcomingReviewsListModel = new DefaultListModel<>();
        JList<Resource> upcomingReviewsList = new JList<>(upcomingReviewsListModel);
        upcomingReviewsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Resource resource = (Resource) value;
                setText(String.format("<html><b>%s</b><br/><small>Due on: %s</small></html>",
                        resource.getTitle(), resource.getNextReviewDate()));
                return this;
            }
        });

//        JButton reviewButton = new JButton("Start Review");   // start review
//        reviewButton.addActionListener(e -> {
//            Resource selectedResource = upcomingReviewsList.getSelectedValue();
//            if (selectedResource != null) {
//                mainFrame.showResourceReview(selectedResource.getId());
//            } else {
//                JOptionPane.showMessageDialog(this, "Please select a resource to review.");
//            }
//        });

        upcomingReviewsPanel.add(new JScrollPane(upcomingReviewsList), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        buttonPanel.add(reviewButton);
        upcomingReviewsPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(welcomeLabel, BorderLayout.NORTH);
        add(upcomingReviewsPanel, BorderLayout.CENTER);

        refreshDashboard();
    }

    public void refreshDashboard() {
        upcomingReviewsListModel.clear();
        List<Resource> dueResources = dataManager.getDueResources();
        dueResources.forEach(upcomingReviewsListModel::addElement);
        if (dueResources.isEmpty()) {
            // Corrected the Resource constructor call by adding courseId and topicId
            upcomingReviewsListModel.addElement(new Resource(UUID.randomUUID(), UUID.randomUUID(), "No upcoming reviews!", "", ResourceType.TEXT, ""));
        }
    }
}
