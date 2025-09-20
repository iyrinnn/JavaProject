import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.awt.Desktop;

public class TopicDetailPanel extends JPanel {
    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private Course currentCourse;
    private Topic currentTopic;
    private JPanel resourceListPanel;
    private DefaultListModel<String> reviewModel;
    private JList<String> reviewHistoryList;
    private JPanel headerPanel;

    public TopicDetailPanel(DataManager dataManager, UUID courseId, UUID topicId, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;

        System.out.println("Creating TopicDetailPanel for course: " + courseId + ", topic: " + topicId);

        this.currentCourse = dataManager.getCourseById(courseId);
        if (this.currentCourse == null) {
            System.err.println("ERROR: Course not found for ID: " + courseId);
            JOptionPane.showMessageDialog(this, "Course not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.currentTopic = currentCourse.getTopicById(topicId);
        if (this.currentTopic == null) {
            System.err.println("ERROR: Topic not found for ID: " + topicId);
            JOptionPane.showMessageDialog(this, "Topic not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        Font headingFont = new Font("Serif", Font.BOLD, 20);
        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 13);

        // Header
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JButton backButton = new JButton("Back");
        styleButton(backButton);
        backButton.setFont(buttonFont);
        backButton.addActionListener(e -> mainFrame.showCourseDetail(currentCourse.getId()));
        headerPanel.add(backButton, BorderLayout.WEST);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(currentTopic.getName());
        titleLabel.setFont(headingFont);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Course: " + currentCourse.getName() +
                " | Next review: " + formatDate(currentTopic.getNextReviewDate()));
        subtitleLabel.setFont(labelFont);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton editTopicButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton reviewButton = new JButton("Start review session");

        styleButton(editTopicButton);
        styleButton(deleteButton);
        styleButton(reviewButton);

        editTopicButton.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(this, "Enter new topic name:", currentTopic.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                currentTopic.setName(newName.trim());
                saveDataSafely();
                mainFrame.showTopicDetail(currentCourse.getId(), currentTopic.getId());
            }
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this topic?", "Delete Topic", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                currentCourse.deleteTopic(currentTopic.getId());
                saveDataSafely();
                mainFrame.showCourseDetail(currentCourse.getId());
            }
        });

        reviewButton.addActionListener(e -> {
            mainFrame.showTopicReview(currentTopic.getId());
        });

        actionPanel.add(editTopicButton);
        actionPanel.add(deleteButton);
        actionPanel.add(reviewButton);

        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // CENTER
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // LEFT BLOCK - Resources
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(235, 235, 235));
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel resourcesLabel = new JLabel("Resources");
        resourcesLabel.setFont(headingFont);

        resourceListPanel = new JPanel();
        resourceListPanel.setLayout(new BoxLayout(resourceListPanel, BoxLayout.Y_AXIS));
        resourceListPanel.setOpaque(false);

        JPanel resButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        resButtonPanel.setOpaque(false);

        JButton addResource = new JButton("Add new resource");
        styleButton(addResource);
        addResource.addActionListener(e -> {
            new ResourceFormDialog(mainFrame, dataManager, currentCourse.getId(), this).setVisible(true);
        });

        resButtonPanel.add(addResource);

        leftPanel.add(resourcesLabel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(resourceListPanel), BorderLayout.CENTER);
        leftPanel.add(resButtonPanel, BorderLayout.SOUTH);

        // RIGHT BLOCK - Review History
        JPanel reviewPanel = new JPanel(new BorderLayout());
        reviewPanel.setPreferredSize(new Dimension(250, 100));
        reviewPanel.setBackground(new Color(235, 235, 235));
        reviewPanel.setBorder(BorderFactory.createTitledBorder("Review History"));

        reviewModel = new DefaultListModel<>();
        reviewHistoryList = new JList<>(reviewModel);
        reviewHistoryList.setFont(labelFont);

        JScrollPane historyScroll = new JScrollPane(reviewHistoryList);
        reviewPanel.add(historyScroll, BorderLayout.CENTER);

        contentPanel.add(leftPanel, BorderLayout.CENTER);
        contentPanel.add(reviewPanel, BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);

        refreshPanel();
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(0xF0F0F0));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void refreshPanel() {
        System.out.println("Refreshing TopicDetailPanel for topic: " + currentTopic.getName());

        if (currentTopic == null || currentCourse == null) {
            return;
        }

        // Update header labels
        JLabel titleLabel = (JLabel) ((JPanel) headerPanel.getComponent(1)).getComponent(0);
        JLabel subtitleLabel = (JLabel) ((JPanel) headerPanel.getComponent(1)).getComponent(2);

        titleLabel.setText(currentTopic.getName());
        subtitleLabel.setText("Course: " + currentCourse.getName() +
                " | Next review: " + formatDate(currentTopic.getNextReviewDate()));

        refreshResources();
        refreshReviewHistory();
    }

    private void refreshResources() {
        resourceListPanel.removeAll();

        if (currentTopic.getResources().isEmpty()) {
            JLabel noResourcesLabel = new JLabel("No resources added yet. Click 'Add new resource' to get started.");
            noResourcesLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            noResourcesLabel.setForeground(Color.GRAY);
            noResourcesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resourceListPanel.add(Box.createVerticalGlue());
            resourceListPanel.add(noResourcesLabel);
            resourceListPanel.add(Box.createVerticalGlue());
        } else {
            for (Resource res : currentTopic.getResources()) {
                JPanel resPanel = new JPanel(new BorderLayout());
                resPanel.setBackground(Color.WHITE);
                resPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                resPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

                // Resource title and type (clickable)
                JLabel title = new JLabel("<html><b>" + res.getName() + "</b> (" + res.getType() + ")</html>");
                title.setBorder(new EmptyBorder(10, 10, 5, 10));
                title.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                title.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                            try {
                                if (!res.getUrl().isEmpty()) {
                                    Desktop.getDesktop().browse(new URI(res.getUrl()));
                                } else {
                                    JOptionPane.showMessageDialog(TopicDetailPanel.this,
                                            "No link available for this resource.",
                                            "No Link", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } catch (IOException | URISyntaxException ex) {
                                JOptionPane.showMessageDialog(TopicDetailPanel.this,
                                        "Could not open link: " + res.getUrl() + "\nError: " + ex.getMessage(),
                                        "Link Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(TopicDetailPanel.this,
                                    "Desktop browsing is not supported on this system.",
                                    "Feature Not Supported", JOptionPane.WARNING_MESSAGE);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        title.setForeground(Color.BLUE);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        title.setForeground(Color.BLACK);
                    }
                });

                resPanel.add(title, BorderLayout.CENTER);

                // Edit button
                JButton editResourceButton = new JButton("Edit");
                styleButton(editResourceButton);
                editResourceButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
                editResourceButton.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                editResourceButton.addActionListener(e -> {
                    new EditResourceDialog(mainFrame, dataManager, currentTopic.getId(), res, this).setVisible(true);
                });

                JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonWrapper.setOpaque(false);
                buttonWrapper.add(editResourceButton);
                resPanel.add(buttonWrapper, BorderLayout.SOUTH);

                resourceListPanel.add(resPanel);
                resourceListPanel.add(Box.createVerticalStrut(8));
            }
        }

        resourceListPanel.revalidate();
        resourceListPanel.repaint();
    }

    private void refreshReviewHistory() {
        reviewModel.clear();

        if (currentTopic.getReviewHistory().isEmpty()) {
            reviewModel.addElement("No review history yet.");
        } else {
            // Display only the review dates in MM/dd/yy format
            currentTopic.getReviewHistory().stream()
                    .sorted((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()))
                    .forEach(review -> {
                        String date = review.getTimestamp().format(DateTimeFormatter.ofPattern("MM/dd/yy"));
                        reviewModel.addElement(date);
                    });
        }
    }

    private String formatDate(java.time.LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    public Topic getCurrentTopic() {
        return currentTopic;
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