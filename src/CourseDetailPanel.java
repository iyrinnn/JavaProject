// CourseDetailPanel.java
// This panel displays the details of a specific course.

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

    public CourseDetailPanel(DataManager dataManager, UUID courseId, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;
        this.currentCourse = dataManager.getCourseById(courseId);

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        // Header Panel (Top part of the design)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        courseNameLabel = new JLabel(currentCourse.getName());
        courseNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(courseNameLabel, BorderLayout.WEST);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerButtons.setOpaque(false);
        JButton editCourseButton = new JButton("Edit");
        JButton deleteCourseButton = new JButton("Delete");

        editCourseButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit functionality not yet implemented."));
        deleteCourseButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this course?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
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

        // Center Content Panel (Summary and Topics)
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);

        // Top section with summary panels
        JPanel summaryPanelWrapper = new JPanel(new GridLayout(1, 2, 20, 0));
        summaryPanelWrapper.setOpaque(false);

        JPanel githubPanel = new JPanel(new BorderLayout());
        githubPanel.setBorder(BorderFactory.createTitledBorder("Github/File activity summary"));
        githubPanel.setBackground(Color.WHITE);
        JTextArea summaryArea = new JTextArea("Total Topics: " + currentCourse.getTopics().size() + "\nTotal Resources: " + dataManager.getAllResourcesInCourse(currentCourse.getId()).size());
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        githubPanel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel totalTopicsAddedPanel = new JPanel(new BorderLayout());
        totalTopicsAddedPanel.setBackground(Color.WHITE);
        totalTopicsAddedPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        JLabel totalTopicsLabel = new JLabel("Total Topic Added: " + currentCourse.getTopics().size());
        totalTopicsAddedPanel.add(totalTopicsLabel, BorderLayout.CENTER);
        statsPanel.add(totalTopicsAddedPanel);

        JPanel totalDuePanel = new JPanel(new BorderLayout());
        totalDuePanel.setBackground(Color.WHITE);
        totalDuePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        JLabel totalDueLabel = new JLabel("Total due: " + dataManager.getDueResourcesByCourse(currentCourse.getId()).size());
        totalDuePanel.add(totalDueLabel, BorderLayout.CENTER);
        statsPanel.add(totalDuePanel);

        JPanel lastReviewPanel = new JPanel(new BorderLayout());
        lastReviewPanel.setBackground(Color.WHITE);
        lastReviewPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        JLabel lastReviewLabel = new JLabel("Last Review: " + (dataManager.getLastReviewDateInCourse(currentCourse.getId()) == null ? "N/A" : dataManager.getLastReviewDateInCourse(currentCourse.getId())));
        lastReviewPanel.add(lastReviewLabel, BorderLayout.CENTER);
        statsPanel.add(lastReviewPanel);

        summaryPanelWrapper.add(githubPanel);
        summaryPanelWrapper.add(statsPanel);

        contentPanel.add(summaryPanelWrapper, BorderLayout.NORTH);

        // Topics section
        JPanel topicsPanel = new JPanel(new BorderLayout(10, 10));
        topicsPanel.setOpaque(false);

        JPanel topicsHeader = new JPanel(new BorderLayout());
        topicsHeader.setOpaque(false);
        JLabel topicsLabel = new JLabel("Topics");
        topicsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topicsHeader.add(topicsLabel, BorderLayout.WEST);

        JButton addTopicButton = new JButton("add new topics");
        addTopicButton.addActionListener(e -> {
            String topicName = JOptionPane.showInputDialog(this, "Enter new topic name:");
            if (topicName != null && !topicName.trim().isEmpty()) {
                currentCourse.addTopic(new Topic(topicName, "Not started"));
                refreshPanel();
                mainFrame.saveDataInBackground();
            }
        });
        topicsHeader.add(addTopicButton, BorderLayout.EAST);
        topicsPanel.add(topicsHeader, BorderLayout.NORTH);

        String[] topicColumnNames = {"Topic Name", "Next review date", "Status"};
        topicsTableModel = new DefaultTableModel(topicColumnNames, 0);
        topicsTable = new JTable(topicsTableModel);
        topicsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = topicsTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        Topic selectedTopic = currentCourse.getTopics().get(row);
                        mainFrame.showTopicDetail(currentCourse.getId(), selectedTopic.getId());
                    }
                }
            }
        });

        topicsPanel.add(new JScrollPane(topicsTable), BorderLayout.CENTER);

        contentPanel.add(topicsPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        refreshPanel();
    }

    public void refreshPanel() {
        courseNameLabel.setText(currentCourse.getName());

        topicsTableModel.setRowCount(0);
        for (Topic topic : currentCourse.getTopics()) {
            topicsTableModel.addRow(new Object[]{topic.getName(), "N/A", topic.getStatus()});
        }
    }
}
