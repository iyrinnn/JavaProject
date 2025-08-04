import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TopicDetailPanel extends JPanel {
    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private Course currentCourse;
    public Topic currentTopic;
    private DefaultTableModel topicInfoTableModel;
    private JTable topicInfoTable;
    private JList<String> reviewHistoryList;

    public TopicDetailPanel(DataManager dataManager, UUID courseId, UUID topicId, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;
        this.currentCourse = dataManager.getCourseById(courseId);
        this.currentTopic = currentCourse.getTopicById(topicId);

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245)); // Light gray background

        Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
        Font headerLabelFont = new Font("Segoe UI", Font.BOLD, 18);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension normalButtonSize = new Dimension(140, 40);
        Dimension wideButtonSize = new Dimension(200, 40);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JButton backButton = new JButton("Back");
        backButton.setFont(buttonFont);
        backButton.setPreferredSize(normalButtonSize);
        styleButton(backButton);
        backButton.addActionListener(e -> mainFrame.showCourseDetail(currentCourse.getId()));
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel topicNameLabel = new JLabel(currentTopic.getName());
        topicNameLabel.setFont(titleFont);
        topicNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(topicNameLabel, BorderLayout.CENTER);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerButtons.setOpaque(false);

        JButton editButton = new JButton("Edit");
        editButton.setFont(buttonFont);
        editButton.setPreferredSize(normalButtonSize);
        styleButton(editButton);
        editButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit functionality not yet implemented."));

        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(buttonFont);
        deleteButton.setPreferredSize(normalButtonSize);
        styleButton(deleteButton);
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this topic?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                currentCourse.deleteTopic(currentTopic.getId());
                mainFrame.showCourseDetail(currentCourse.getId());
                mainFrame.saveDataInBackground();
            }
        });

        JButton startReviewButton = new JButton("Start review session");
        startReviewButton.setFont(buttonFont);
        startReviewButton.setPreferredSize(wideButtonSize);
        styleButton(startReviewButton);
        startReviewButton.addActionListener(e -> {
            // Show topic review panel (assuming you have one)
            mainFrame.showTopicReview(currentTopic.getId());
        });

        headerButtons.add(editButton);
        headerButtons.add(deleteButton);
        headerButtons.add(startReviewButton);
        headerPanel.add(headerButtons, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Content Panel: Show topic attributes in table and review history in list
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        // Topic Info Table Panel
        JPanel topicInfoPanel = new JPanel(new BorderLayout(10, 10));
        topicInfoPanel.setOpaque(false);

        JLabel infoLabel = new JLabel("Topic Information");
        infoLabel.setFont(headerLabelFont);
        topicInfoPanel.add(infoLabel, BorderLayout.NORTH);

        String[] columnNames = {"Attribute", "Value"};
        topicInfoTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells
            }
        };
        topicInfoTable = new JTable(topicInfoTableModel);
        topicInfoTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topicInfoTable.setRowHeight(28);

        topicInfoPanel.add(new JScrollPane(topicInfoTable), BorderLayout.CENTER);

        contentPanel.add(topicInfoPanel);

        // Review History Panel
        JPanel reviewHistoryPanel = new JPanel(new BorderLayout(10, 10));
        reviewHistoryPanel.setBorder(BorderFactory.createTitledBorder("Review History"));
        reviewHistoryPanel.setBackground(Color.WHITE);

        reviewHistoryList = new JList<>();
        reviewHistoryList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reviewHistoryPanel.add(new JScrollPane(reviewHistoryList), BorderLayout.CENTER);

        contentPanel.add(reviewHistoryPanel);

        add(contentPanel, BorderLayout.CENTER);

        refreshPanel();
    }

    private void styleButton(JButton button) {
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBackground(new Color(0xF7F6F3)); // Notion off-white background
        button.setForeground(new Color(0x2E2E2E)); // Dark text
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0xEBEBE9)); // subtle hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0xF7F6F3));
            }
        });
    }

    public void refreshPanel() {
        // Clear table
        topicInfoTableModel.setRowCount(0);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        // Add rows for topic attributes
        topicInfoTableModel.addRow(new Object[]{"Name", currentTopic.getName()});
        topicInfoTableModel.addRow(new Object[]{"Status", currentTopic.getStatus()});
        topicInfoTableModel.addRow(new Object[]{"Went Online", dtf.format(currentTopic.getWentOnlineDate())});
        topicInfoTableModel.addRow(new Object[]{"Next Review Date", currentTopic.getNextReviewDate() != null ? dtf.format(currentTopic.getNextReviewDate()) : "N/A"});

        // Refresh review history list
        DefaultListModel<String> historyModel = new DefaultListModel<>();
        currentTopic.getReviewHistory().forEach(review -> historyModel.addElement(review.toString()));
        reviewHistoryList.setModel(historyModel);
    }

    public Topic getCurrentTopic() {
        return this.currentTopic;
    }
}
