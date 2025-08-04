import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CourseDetailPanel extends JPanel {
    private UUID courseId;
    private DataManager dataManager;
    private MainApplicationFrame mainApp;
    private Course course;
    private JPanel topicListPanel;

    public CourseDetailPanel(DataManager dataManager, UUID courseId, MainApplicationFrame mainApp) {
        this.dataManager = dataManager;
        this.courseId = courseId;
        this.mainApp = mainApp;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        this.course = dataManager.getCourseById(courseId);

        initUI();
    }

    private void initUI() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        headerPanel.setBackground(Color.WHITE);

        JLabel courseLabel = new JLabel(course.getName());
        courseLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(courseLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // --- Topic List Panel ---
        topicListPanel = new JPanel();
        topicListPanel.setLayout(new BoxLayout(topicListPanel, BoxLayout.Y_AXIS));
        topicListPanel.setBackground(Color.WHITE);
        topicListPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        JScrollPane scrollPane = new JScrollPane(topicListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        updateTopicList();
    }

    public void updateTopicList() {
        topicListPanel.removeAll();
        List<Topic> topics = course.getTopics();

        for (Topic topic : topics) {
            TopicItemPanel item = new TopicItemPanel(topic);
            topicListPanel.add(item);
            topicListPanel.add(Box.createVerticalStrut(12));
        }

        topicListPanel.revalidate();
        topicListPanel.repaint();
    }

    private void openTopicDetail(Topic topic) {
        mainApp.showTopicDetail(courseId, topic.getId());
    }

    private void editTopic(Topic topic) {
        String newName = JOptionPane.showInputDialog(this, "Edit topic name:", topic.getName());
        if (newName != null && !newName.trim().isEmpty()) {
            topic.setName(newName.trim());
            try {
                dataManager.saveData();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save changes.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            updateTopicList();
        }
    }

    private void deleteTopic(Topic topic) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete topic: " + topic.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            course.deleteTopic(topic.getId());
            try {
                dataManager.saveData();
// Save changes
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save changes.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            updateTopicList();
        }
    }

    // Custom Topic Item Panel
    class TopicItemPanel extends JPanel {
        private JButton editButton;
        private JButton deleteButton;

        public TopicItemPanel(Topic topic) {
            setLayout(new BorderLayout());
            setBackground(new Color(248, 249, 250)); // Light gray like Notion
            setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel nameLabel = new JLabel(topic.getName());
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            nameLabel.setForeground(Color.DARK_GRAY);
            nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            nameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openTopicDetail(topic);
                }
            });

            // Buttons panel (right aligned)
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            buttonsPanel.setOpaque(false);

            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");

            styleButton(editButton);
            styleButton(deleteButton);

            editButton.setVisible(false);
            deleteButton.setVisible(false);

            editButton.addActionListener(e -> editTopic(topic));
            deleteButton.addActionListener(e -> deleteTopic(topic));

            buttonsPanel.add(editButton);
            buttonsPanel.add(deleteButton);

            add(nameLabel, BorderLayout.WEST);
            add(buttonsPanel, BorderLayout.EAST);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    editButton.setVisible(true);
                    deleteButton.setVisible(true);
                    setBackground(new Color(233, 236, 239)); // Slight hover color
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    editButton.setVisible(false);
                    deleteButton.setVisible(false);
                    setBackground(new Color(248, 249, 250));
                }
            });
        }

        private void styleButton(JButton button) {
            button.setFont(new Font("SansSerif", Font.PLAIN, 13));
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }
}
