import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class CourseDetailPanel extends JPanel {
    private MainApplicationFrame mainApp;
    private Course course;
    private DataManager dataManager;
    private JPanel topicListPanel;

    public CourseDetailPanel(MainApplicationFrame mainApp, Course course, DataManager dataManager) {
        this.mainApp = mainApp;
        this.course = course;
        this.dataManager = dataManager;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createStatsPanel(), BorderLayout.CENTER);
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

        statsBox.add(createStatCard("Total Topic Added:", String.valueOf(course.getTopics().size())));
        statsBox.add(createStatCard("Total Due:", "3")); // Replace with actual logic
        statsBox.add(createStatCard("Last Review:", "2 days ago")); // Replace with actual logic

        panel.add(statsBox, BorderLayout.NORTH);
        panel.add(createTopicSection(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setBackground(new Color(240, 240, 240));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("SansSerif", Font.BOLD, 16));

        card.add(labelText, BorderLayout.NORTH);
        card.add(valueText, BorderLayout.CENTER);
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
                course.addTopic(new Topic(name.trim(), course.getId().toString()));
                saveDataSafely();
                refreshTopicList();
            }
        });

        top.add(title, BorderLayout.WEST);
        top.add(addButton, BorderLayout.EAST);

        topicListPanel = new JPanel();
        topicListPanel.setLayout(new BoxLayout(topicListPanel, BoxLayout.Y_AXIS));
        topicListPanel.setBackground(new Color(245, 245, 245));
        topicListPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        refreshTopicList();

        JScrollPane scrollPane = new JScrollPane(topicListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        section.add(top, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    // ✅ THIS WAS MISSING
    public void refreshTopicList() {
        topicListPanel.removeAll();

        for (Topic topic : course.getTopics()) {
            JPanel row = new JPanel(new GridLayout(1, 3));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            row.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            row.setBackground(new Color(255, 240, 240));
            row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel name = new JLabel(topic.getName());
            JLabel date = new JLabel(topic.getNextReviewDate() != null ? topic.getNextReviewDate().toString() : "N/A");
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
