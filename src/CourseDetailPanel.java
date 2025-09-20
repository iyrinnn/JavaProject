import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CourseDetailPanel extends JPanel {
    private MainApplicationFrame mainApp;
    private Course course;
    private DataManager dataManager;
    private JPanel topicListPanel;
    private JLabel totalTopicsAddedLabel;
    private JLabel totalTopicsDueLabel;
    private JLabel lastReviewLabel;

    public CourseDetailPanel(MainApplicationFrame mainApp, Course course, DataManager dataManager) {
        this.mainApp = mainApp;
        this.course = course;
        this.dataManager = dataManager;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(createHeader(), BorderLayout.NORTH);
        add(createStatsPanel(), BorderLayout.CENTER);
        refreshPanel();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        header.setBackground(Color.WHITE);

        JLabel courseTitle = new JLabel(course.getName());
        courseTitle.setFont(new Font("Serif", Font.BOLD, 28));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        editButton.setFocusPainted(false);
        deleteButton.setFocusPainted(false);

        editButton.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(this, "Enter new course name:", course.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                course.setName(newName.trim());
                saveDataSafely();
                mainApp.updateSidebar();
                ((JLabel) ((BorderLayout) header.getLayout()).getLayoutComponent(BorderLayout.WEST)).setText(course.getName());
            }
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this course?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainApp.deleteCourse(course.getId());
            }
        });

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        header.add(courseTitle, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel statsBox = new JPanel(new GridLayout(1, 3, 20, 0));
        statsBox.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        statsBox.setBackground(Color.WHITE);

        totalTopicsAddedLabel = new JLabel("0", SwingConstants.CENTER);
        statsBox.add(createStatCard("Total Topic Added:", totalTopicsAddedLabel));

        totalTopicsDueLabel = new JLabel("0", SwingConstants.CENTER);
        statsBox.add(createStatCard("Total Due:", totalTopicsDueLabel));

        lastReviewLabel = new JLabel("N/A", SwingConstants.CENTER);
        statsBox.add(createStatCard("Last Review:", lastReviewLabel));

        panel.add(statsBox, BorderLayout.NORTH);
        panel.add(createTopicSection(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String label, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setBackground(new Color(240, 240, 240));

        JLabel labelText = new JLabel(label, SwingConstants.CENTER);
        labelText.setFont(new Font("SansSerif", Font.PLAIN, 14));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        card.add(labelText, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTopicSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(new Color(245, 245, 245));
        section.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Topics");
        title.setFont(new Font("Serif", Font.BOLD, 24));

        JButton addButton = new JButton("Add new topic");
        addButton.setFocusPainted(false);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter topic name:");
            if (name != null && !name.trim().isEmpty()) {
                course.addTopic(new Topic(name.trim(), "New"));
                saveDataSafely();
                refreshPanel();
            }
        });

        top.add(title, BorderLayout.WEST);
        top.add(addButton, BorderLayout.EAST);

        topicListPanel = new JPanel();
        topicListPanel.setLayout(new BoxLayout(topicListPanel, BoxLayout.Y_AXIS));
        topicListPanel.setBackground(new Color(245, 245, 245));
        topicListPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(topicListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        section.add(top, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);
        return section;
    }

    public void refreshPanel() {
        // Update stats
        totalTopicsAddedLabel.setText(String.valueOf(course.getTopics().size()));

        long dueCount = course.getTopics().stream().filter(Topic::isDue).count();
        totalTopicsDueLabel.setText(String.valueOf(dueCount));

        // FIXED: Get the LAST review date from ANY topic in the ENTIRE app
        Optional<LocalDate> lastReview = dataManager.getAllCourses().stream()
                .flatMap(c -> c.getTopics().stream())          // Get all topics from all courses
                .flatMap(topic -> topic.getReviewHistory().stream()) // Get all reviews from all topics
                .map(review -> review.getTimestamp().toLocalDate()) // Convert to LocalDate
                .max(LocalDate::compareTo);                    // Find the most recent date

        lastReviewLabel.setText(lastReview.isPresent() ?
                lastReview.get().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A");

        // Refresh topic list
        refreshTopicList();
    }

    public void refreshTopicList() {
        topicListPanel.removeAll();

        if (course.getTopics().isEmpty()) {
            JLabel noTopicsLabel = new JLabel("No topics added yet. Click 'Add new topic' to get started.");
            noTopicsLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            noTopicsLabel.setForeground(Color.GRAY);
            noTopicsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            topicListPanel.add(Box.createVerticalGlue());
            topicListPanel.add(noTopicsLabel);
            topicListPanel.add(Box.createVerticalGlue());
        } else {
            for (Topic topic : course.getTopics()) {
                JPanel row = new JPanel(new GridLayout(1, 3));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                row.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                row.setBackground(new Color(255, 240, 240));
                row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                JLabel name = new JLabel(topic.getName());
                JLabel date = new JLabel(topic.getNextReviewDate() != null ?
                        topic.getNextReviewDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A");
                JLabel status = new JLabel(topic.isDue() ? "Due" : "Up to date");

                name.setFont(new Font("SansSerif", Font.PLAIN, 14));
                date.setFont(new Font("SansSerif", Font.PLAIN, 14));
                status.setFont(new Font("SansSerif", Font.PLAIN, 14));

                row.add(name);
                row.add(date);
                row.add(status);

                row.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("Topic clicked: " + topic.getName() + " ID: " + topic.getId());
                        mainApp.showTopicDetail(course.getId(), topic.getId());
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        row.setBackground(new Color(255, 230, 230));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        row.setBackground(new Color(255, 240, 240));
                    }
                });

                topicListPanel.add(row);
                topicListPanel.add(Box.createVerticalStrut(8));
            }
        }

        topicListPanel.revalidate();
        topicListPanel.repaint();
    }

    private void saveDataSafely() {
        try {
            dataManager.saveData();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}