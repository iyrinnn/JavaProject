import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class TopicDetailPanel extends JPanel {
    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private Course currentCourse;
    public Topic currentTopic;
    private DefaultTableModel resourcesTableModel;

    private JTable resourcesTable;



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
            Resource firstDueResource = dataManager.getFirstDueResourceInTopic(currentTopic.getId());
            if (firstDueResource != null) {
                mainFrame.showResourceReview(firstDueResource.getId());
            } else {
                JOptionPane.showMessageDialog(this, "No resources due for review in this topic.");
            }
        });

        headerButtons.add(editButton);
        headerButtons.add(deleteButton);
        headerButtons.add(startReviewButton);
        headerPanel.add(headerButtons, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        // Resources Panel
        JPanel resourcesPanel = new JPanel(new BorderLayout(10, 10));
        resourcesPanel.setOpaque(false);

        JPanel resourcesHeader = new JPanel(new BorderLayout());
        resourcesHeader.setOpaque(false);

        JLabel resourcesLabel = new JLabel("Resources");
        resourcesLabel.setFont(headerLabelFont);
        resourcesHeader.add(resourcesLabel, BorderLayout.WEST);

        JButton addResourceButton = new JButton("Add new resource");
        addResourceButton.setFont(buttonFont);
        addResourceButton.setPreferredSize(new Dimension(160, 40));
        styleButton(addResourceButton);
        addResourceButton.addActionListener(e -> {
            ResourceFormDialog dialog = new ResourceFormDialog(mainFrame, dataManager, currentCourse.getId(), this);
            dialog.setVisible(true);
        });

        resourcesHeader.add(addResourceButton, BorderLayout.EAST);
        resourcesPanel.add(resourcesHeader, BorderLayout.NORTH);

        String[] resourceColumnNames = {"Title", "Type", "Next Review"};
        resourcesTableModel = new DefaultTableModel(resourceColumnNames, 0);
        resourcesTable = new JTable(resourcesTableModel);
        resourcesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resourcesTable.setRowHeight(28);

        resourcesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = resourcesTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        Resource selectedResource = currentTopic.getResources().get(row);
                        mainFrame.showResourceReview(selectedResource.getId());
                    }
                }
            }
        });

        resourcesPanel.add(new JScrollPane(resourcesTable), BorderLayout.CENTER);
        contentPanel.add(resourcesPanel);

        // Review History Panel
        JPanel reviewHistoryPanel = new JPanel(new BorderLayout(10, 10));
        reviewHistoryPanel.setBorder(BorderFactory.createTitledBorder("Review History"));
        reviewHistoryPanel.setBackground(Color.WHITE);

        JList<String> historyList = new JList<>();
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText("Review " + (index + 1) + ": " + value);
                return this;
            }
        });

        resourcesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = resourcesTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        Resource selectedResource = currentTopic.getResources().get(row);
                        DefaultListModel<String> historyModel = new DefaultListModel<>();
                        selectedResource.getReviewHistory().forEach(review -> historyModel.addElement(review.toString()));
                        historyList.setModel(historyModel);
                    }
                }
            }
        });

        reviewHistoryPanel.add(new JScrollPane(historyList), BorderLayout.CENTER);

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
        resourcesTableModel.setRowCount(0);
        for (Resource resource : currentTopic.getResources()) {
            resourcesTableModel.addRow(new Object[]{resource.getTitle(), resource.getType(), resource.getNextReviewDate()});
        }
    }
}
