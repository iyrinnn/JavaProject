import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class CourseDetailPanel extends JPanel {
    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private Course currentCourse;
    private DefaultTableModel topicsTableModel;
    private JTable topicsTable;
    private JLabel courseNameLabel;

    private JLabel resourcesDueLabel;
    private JLabel totalCoursesLabel;
    private JLabel lastReviewLabel;

    public CourseDetailPanel(DataManager dataManager, UUID courseId, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;
        this.currentCourse = dataManager.getCourseById(courseId);

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(247, 246, 243)); // Notion-like background

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        courseNameLabel = new JLabel(currentCourse.getName());
        courseNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerPanel.add(courseNameLabel, BorderLayout.WEST);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerButtons.setOpaque(false);

        JButton editCourseButton = new JButton("Edit");
        editCourseButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton deleteCourseButton = new JButton("Delete");
        deleteCourseButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        editCourseButton.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(this, "Edit course name:", currentCourse.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                currentCourse.setName(newName.trim());
                courseNameLabel.setText(newName.trim());
                mainFrame.saveDataInBackground();
            }
        });

        deleteCourseButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this course?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dataManager.deleteCourse(currentCourse.getId());
                mainFrame.updateSidebar();
                mainFrame.showDashboard();
                mainFrame.saveDataInBackground();
            }
        });

        headerButtons.add(editCourseButton);
        headerButtons.add(deleteCourseButton);
        headerPanel.add(headerButtons, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Stats
        JPanel statsPanel = new JPanel(null);
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(900, 120));

        JPanel resourcesPanel = createStatPanel("Resources Due Today",
                String.valueOf(dataManager.getDueResourcesByCourse(currentCourse.getId()).size()),
                new Color(255, 160, 122), 10, 10, 250, 100);
        resourcesDueLabel = (JLabel) resourcesPanel.getComponent(1);
        statsPanel.add(resourcesPanel);

        JPanel totalCoursesPanel = createStatPanel("Total Topics",
                String.valueOf(currentCourse.getTopics().size()),
                new Color(135, 206, 250), 270, 10, 250, 100);
        totalCoursesLabel = (JLabel) totalCoursesPanel.getComponent(1);
        statsPanel.add(totalCoursesPanel);

        String lastReviewDate = String.valueOf(dataManager.getLastReviewDateInCourse(currentCourse.getId()));
        if (lastReviewDate == null) lastReviewDate = "N/A";
        JPanel lastReviewPanel = createStatPanel("Last Review", lastReviewDate,
                new Color(144, 238, 144), 530, 10, 350, 100);
        lastReviewLabel = (JLabel) lastReviewPanel.getComponent(1);
        statsPanel.add(lastReviewPanel);

        add(statsPanel, BorderLayout.CENTER);

        // Topics Section
        JPanel topicsPanel = new JPanel(new BorderLayout(10, 10));
        topicsPanel.setOpaque(false);

        JLabel topicsLabel = new JLabel("Topics");
        topicsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topicsPanel.add(topicsLabel, BorderLayout.NORTH);

        String[] topicColumnNames = {"Topic Name", "Next review date", "Status"};
        topicsTableModel = new DefaultTableModel(topicColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        topicsTable = new JTable(topicsTableModel);
        topicsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topicsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        topicsTable.setRowHeight(32);
        topicsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        topicsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = topicsTable.getSelectedRow();
                if (row >= 0 && e.getClickCount() == 1) {
                    String topicName = (String) topicsTable.getValueAt(row, 0);
                    Topic selected = currentCourse.getTopics().stream()
                            .filter(t -> t.getName().equals(topicName))
                            .findFirst().orElse(null);
                    if (selected != null) {
                        mainFrame.showTopicDetail(currentCourse.getId(), selected.getId());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(topicsTable);
        topicsPanel.add(scrollPane, BorderLayout.CENTER);

        // Add Topic Button
        JButton addTopicButton = new JButton("Add New Topic");
        addTopicButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addTopicButton.addActionListener(e -> {
            String topicName = JOptionPane.showInputDialog(this, "Enter new topic name:");
            if (topicName != null && !topicName.trim().isEmpty()) {
                currentCourse.addTopic(new Topic(topicName.trim(), "Not started"));
                refreshPanel();
                mainFrame.saveDataInBackground();
            }
        });

        JPanel addTopicPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addTopicPanel.setOpaque(false);
        addTopicPanel.add(addTopicButton);
        topicsPanel.add(addTopicPanel, BorderLayout.SOUTH);

        add(topicsPanel, BorderLayout.SOUTH);

        refreshPanel();
    }

    private JPanel createStatPanel(String title, String value, Color bgColor, int x, int y, int width, int height) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBounds(x, y, width, height);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    public void refreshPanel() {
        courseNameLabel.setText(currentCourse.getName());
        resourcesDueLabel.setText(String.valueOf(dataManager.getDueResourcesByCourse(currentCourse.getId()).size()));
        totalCoursesLabel.setText(String.valueOf(currentCourse.getTopics().size()));

        String lastReview = String.valueOf(dataManager.getLastReviewDateInCourse(currentCourse.getId()));
        lastReviewLabel.setText(lastReview == null ? "N/A" : lastReview);

        topicsTableModel.setRowCount(0);
        for (Topic topic : currentCourse.getTopics()) {
            topicsTableModel.addRow(new Object[]{topic.getName(), "N/A", topic.getStatus()});
        }
    }
}
