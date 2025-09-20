import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        // Header panel
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

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Status and review date
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

        // Rating buttons 0-5
        String[] ratings = {"0 (Blackout)", "1 (Very Poor)", "2 (Poor)", "3 (Good)", "4 (Very Good)", "5 (Perfect)"};
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
        LocalDate today = LocalDate.now();
        int intervalDays;

        // Spaced repetition logic
        switch (recallRating) {
            case 0: intervalDays = 1; break;  // Blackout
            case 1: intervalDays = 2; break;  // Very Poor
            case 2: intervalDays = 4; break;  // Poor
            case 3: intervalDays = 8; break;  // Good
            case 4: intervalDays = 16; break; // Very Good
            case 5: intervalDays = 32; break; // Perfect
            default: intervalDays = 7;
        }

        LocalDate newNextReviewDate = today.plusDays(intervalDays);
        currentTopic.setNextReviewDate(newNextReviewDate);
        currentTopic.setStatus("Reviewed");

        // Add review record with current timestamp
        Review review = new Review(LocalDateTime.now(), recallRating);
        currentTopic.addReviewRecord(review);

        try {
            dataManager.saveData();
            System.out.println("Review saved: " + review.getTimestamp() + " - Rating: " + recallRating);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save review data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Topic review saved! Next review date updated to " +
                newNextReviewDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + ".");

        Course course = dataManager.getCourseForTopic(currentTopic);
        if (course != null) {
            mainFrame.showCourseDetail(course.getId());
        }
    }
}