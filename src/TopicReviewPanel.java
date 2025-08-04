import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TopicReviewPanel extends JPanel {
    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private Topic currentTopic;

    private JLabel nextReviewLabel;
    private JLabel statusLabel;

    public TopicReviewPanel(DataManager dataManager, UUID topicId, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;
        this.currentTopic = dataManager.getTopicById(topicId);

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        // Header panel with Back button and title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(e -> {
            Course course = dataManager.getCourseForTopic(currentTopic);
            if (course != null) {
                mainFrame.showCourseDetail(course.getId());
            }
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Review Topic: " + currentTopic.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Content panel with details and recall rating buttons
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Topic Status and Next Review Date
        statusLabel = new JLabel("Status: " + (currentTopic.isDue() ? "Due" : "Up to date"));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        LocalDate nextReviewDate = currentTopic.getNextReviewDate();
        String nextReviewText = nextReviewDate != null
                ? "Next Review Date: " + nextReviewDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                : "Next Review Date: N/A";
        nextReviewLabel = new JLabel(nextReviewText);
        nextReviewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        nextReviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(statusLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(nextReviewLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Review prompt
        JLabel promptLabel = new JLabel("How well do you recall this topic?");
        promptLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(promptLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Recall rating buttons 0-5
        String[] ratings = {"0 (Blackout)", "1", "2", "3", "4", "5 (Perfect)"};
        for (int i = 0; i < ratings.length; i++) {
            JButton ratingButton = new JButton(ratings[i]);
            ratingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            ratingButton.setMaximumSize(new Dimension(250, 35));
            final int recallRating = i;
            ratingButton.addActionListener(e -> {
                markTopicReviewed(recallRating);
            });
            contentPanel.add(ratingButton);
            contentPanel.add(Box.createVerticalStrut(10));
        }

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(contentPanel);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void markTopicReviewed(int recallRating) {
        // You need to implement spaced repetition update logic inside Topic,
        // here is a simple example that sets next review date based on rating:

        LocalDate today = LocalDate.now();

        // Simple spaced repetition logic (you can customize):
        int intervalDays;
        if (recallRating < 3) {
            intervalDays = 1;
        } else if (recallRating == 3) {
            intervalDays = 3;
        } else if (recallRating == 4) {
            intervalDays = 7;
        } else {
            intervalDays = 14;
        }

        LocalDate newNextReviewDate = today.plusDays(intervalDays);
        currentTopic.setNextReviewDate(newNextReviewDate);

        // Optionally update status
        currentTopic.setStatus("Reviewed");

        // Save data
        try {
            dataManager.saveData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save review data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Topic review saved! Next review date updated to " +
                newNextReviewDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + ".");

        // Navigate back to course detail
        Course course = dataManager.getCourseForTopic(currentTopic);
        if (course != null) {
            mainFrame.showCourseDetail(course.getId());
        }
    }
}
