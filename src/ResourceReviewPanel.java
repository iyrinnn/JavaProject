// ResourceReviewPanel.java
// This panel is for reviewing a specific resource.

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class ResourceReviewPanel extends JPanel {
    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private Resource currentResource;
    private JEditorPane resourceContentPane;

    public ResourceReviewPanel(DataManager dataManager, UUID resourceId, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;
        this.currentResource = dataManager.getResourceById(resourceId);

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            Topic topic = dataManager.getTopicById(currentResource.getTopicId());
            if (topic != null) {
                mainFrame.showTopicDetail(currentResource.getCourseId(), topic.getId());
            } else {
                mainFrame.showCourseDetail(currentResource.getCourseId());
            }
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Review session");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        JPanel reviewContentPanel = new JPanel(new BorderLayout(10, 10));
        reviewContentPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        reviewContentPanel.setBackground(new Color(250, 250, 250));

        resourceContentPane = new JEditorPane();
        resourceContentPane.setEditable(false);
        resourceContentPane.setContentType("text/html");
        resourceContentPane.setText("<html><body>" + currentResource.getContent() + "</body></html>");

        JScrollPane scrollPane = new JScrollPane(resourceContentPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        reviewContentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel recallPanel = new JPanel();
        recallPanel.setLayout(new BoxLayout(recallPanel, BoxLayout.Y_AXIS));
        recallPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        recallPanel.setBackground(new Color(250, 250, 250));

        JLabel promptLabel = new JLabel("How well do you recall this?");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        recallPanel.add(promptLabel);
        recallPanel.add(Box.createVerticalStrut(10));

        // Using a 0-5 scale for the standard SM-2 algorithm
        String[] ratings = {"0 (Blackout)", "1", "2", "3", "4", "5 (Perfect)"};
        for (int i = 0; i < ratings.length; i++) {
            JButton ratingButton = new JButton(ratings[i]);
            ratingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            ratingButton.setMinimumSize(new Dimension(200, 30));
            ratingButton.setMaximumSize(new Dimension(200, 30));
            ratingButton.setPreferredSize(new Dimension(200, 30));

            final int recallRating = i;
            ratingButton.addActionListener(e -> {
                currentResource.markReviewed(recallRating);
                mainFrame.saveDataInBackground();
                JOptionPane.showMessageDialog(this, "Resource review saved! Next review date updated.");
                Topic topic = dataManager.getTopicById(currentResource.getTopicId());
                if (topic != null) {
                    mainFrame.showTopicDetail(currentResource.getCourseId(), topic.getId());
                } else {
                    mainFrame.showCourseDetail(currentResource.getCourseId());
                }
            });
            recallPanel.add(ratingButton);
            recallPanel.add(Box.createVerticalStrut(5));
        }

        reviewContentPanel.add(recallPanel, BorderLayout.SOUTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(reviewContentPanel);
        add(centerWrapper, BorderLayout.CENTER);
    }
}
