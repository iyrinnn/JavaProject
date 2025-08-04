import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class DashboardPanel extends JPanel {

    private DataManager dataManager;
    private MainApplicationFrame mainFrame;

    private JLabel dueTopicsCountLabel;
    private JLabel totalCoursesCountLabel;
    private JLabel lastReviewDateLabel;
    private JPanel dueTopicsPanel;

    public DashboardPanel(DataManager dataManager, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(new Color(248, 248, 255));

        initComponents();
        refreshDashboard();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content ---
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // --- Summary Cards ---
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setOpaque(false);

        summaryPanel.add(createSummaryCard("Topics Due Today", "0", new Color(255, 160, 122)));
        summaryPanel.add(createSummaryCard("Total Courses", "0", new Color(135, 206, 250)));
        summaryPanel.add(createSummaryCard("Last Review", "N/A", new Color(144, 238, 144)));

        mainContent.add(summaryPanel);
        mainContent.add(Box.createVerticalStrut(30));

        // --- Due Topics Section ---
        JPanel dueTopicsSection = new JPanel(new BorderLayout());
        dueTopicsSection.setOpaque(false);
        dueTopicsSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dueHeader = new JLabel("Topics Due Soon");
        dueHeader.setFont(new Font("Arial", Font.BOLD, 22));
        dueTopicsSection.add(dueHeader, BorderLayout.NORTH);

        dueTopicsPanel = new JPanel();
        dueTopicsPanel.setLayout(new BoxLayout(dueTopicsPanel, BoxLayout.Y_AXIS));
        dueTopicsPanel.setBackground(Color.WHITE);
        dueTopicsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));

        JScrollPane scrollPane = new JScrollPane(dueTopicsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(800, 200));

        dueTopicsSection.add(scrollPane, BorderLayout.CENTER);
        mainContent.add(dueTopicsSection);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createSummaryCard(String title, String value, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 40));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        if (title.equals("Topics Due Today")) {
            this.dueTopicsCountLabel = valueLabel;
        } else if (title.equals("Total Courses")) {
            this.totalCoursesCountLabel = valueLabel;
        } else if (title.equals("Last Review")) {
            this.lastReviewDateLabel = valueLabel;
        }

        return card;
    }

    public void refreshDashboard() {
        // --- Update Summary Cards ---
        List<Topic> dueTopics = dataManager.getDueTopics();
        dueTopicsCountLabel.setText(String.valueOf(dueTopics.size()));
        totalCoursesCountLabel.setText(String.valueOf(dataManager.getAllCourses().size()));

        List<LocalDate> dates = dataManager.getAllTopicReviewDates();
        dates.stream()
                .max(LocalDate::compareTo)
                .ifPresentOrElse(
                        date -> lastReviewDateLabel.setText(date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))),
                        () -> lastReviewDateLabel.setText("N/A")
                );

        // --- Populate Due Topics ---
        dueTopicsPanel.removeAll();
        dueTopics.sort(Comparator.comparing(Topic::getNextReviewDate));
        int limit = Math.min(5, dueTopics.size());

        if (dueTopics.isEmpty()) {
            dueTopicsPanel.add(createEmptyLabel("No upcoming topic reviews. Great job!"));
        } else {
            for (int i = 0; i < limit; i++) {
                Topic topic = dueTopics.get(i);
                Course parentCourse = dataManager.getCourseForTopic(topic);
                dueTopicsPanel.add(createDueTopicPanel(topic, parentCourse));
                if (i < limit - 1) dueTopicsPanel.add(Box.createVerticalStrut(5));
            }
        }

        dueTopicsPanel.revalidate();
        dueTopicsPanel.repaint();
    }

    private JLabel createEmptyLabel(String message) {
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.ITALIC, 14));
        label.setForeground(Color.GRAY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(new EmptyBorder(50, 0, 50, 0));
        return label;
    }

    private JPanel createDueTopicPanel(Topic topic, Course parentCourse) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(255, 250, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel topicTitle = new JLabel("<html><b>" + topic.getName() + "</b></html>");
        topicTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(topicTitle, BorderLayout.WEST);

        String courseName = (parentCourse != null) ? parentCourse.getName() : "Unknown Course";
        JLabel infoLabel = new JLabel(String.format("Due: %s | Course: %s",
                topic.getNextReviewDate().format(DateTimeFormatter.ofPattern("MMM dd")),
                courseName));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(Color.DARK_GRAY);
        panel.add(infoLabel, BorderLayout.CENTER);

        JButton reviewButton = new JButton("Review");
        reviewButton.setFont(new Font("Arial", Font.BOLD, 12));
        reviewButton.setBackground(new Color(255, 160, 122));
        reviewButton.setForeground(Color.WHITE);
        reviewButton.setFocusPainted(false);
        reviewButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        reviewButton.addActionListener(e -> mainFrame.showTopicReview(topic.getId()));
        panel.add(reviewButton, BorderLayout.EAST);

        return panel;
    }
}
