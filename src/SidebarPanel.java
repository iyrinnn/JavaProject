import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class SidebarPanel extends JPanel {
    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private DefaultListModel<Course> courseListModel;
    private JList<Course> courseList;

    public SidebarPanel(DataManager dataManager, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(0xF7F6F3)); // Notion sidebar background (off-white)

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false); // Transparent to show parent's background

        JLabel userNameLabel = new JLabel("Irin Sultana & Sujana Farid");
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Notion uses 14pt bold for sidebar user label
        userNameLabel.setForeground(new Color(0x2E2E2E)); // Notion dark gray text
        headerPanel.add(userNameLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        // Use light gray buttons with subtle hover effect
        JButton homeButton = createSidebarButton("Home");
        homeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Bold 14pt for buttons
        homeButton.addActionListener(e -> mainFrame.showDashboard());
        headerPanel.add(homeButton);
        headerPanel.add(Box.createVerticalStrut(5));

        JButton addCourseButton = createSidebarButton("Add new course");
        addCourseButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addCourseButton.addActionListener(e -> {
            AddCourseDialog dialog = new AddCourseDialog(mainFrame, dataManager, this);
            dialog.setVisible(true);
        });
        headerPanel.add(addCourseButton);
        headerPanel.add(Box.createVerticalStrut(20));

        add(headerPanel, BorderLayout.NORTH);

        JPanel courseListPanel = new JPanel(new BorderLayout());
        courseListPanel.setOpaque(false);

        JLabel courseTitleLabel = new JLabel("Courses");
        courseTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Section header 16pt bold
        courseTitleLabel.setForeground(new Color(0x2E2E2E));
        courseListPanel.add(courseTitleLabel, BorderLayout.NORTH);

        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseList.setFixedCellHeight(40);
        courseList.setBackground(new Color(0xF7F6F3)); // Notion sidebar background
        courseList.setForeground(new Color(0x2E2E2E)); // Dark gray text
        courseList.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // List items regular 14pt

        // Custom renderer to apply Notion selected and unselected colors + fonts
        courseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Course course = (Course) value;
                setText(course.getName());
                setFont(new Font("Segoe UI", Font.BOLD, 14));  // changed from PLAIN to BOLD
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (isSelected) {
                    setBackground(new Color(0xE3E2DF)); // Selected background
                    setForeground(Color.BLACK); // Selected text black
                } else {
                    setBackground(new Color(0xF7F6F3)); // Not selected background
                    setForeground(new Color(0x2E2E2E));
                }
                return this;
            }
        });

        // Optional: Add hover effect for list items (not default in Swing)
        courseList.addMouseMotionListener(new MouseAdapter() {
            private int hoveredIndex = -1;

            @Override
            public void mouseMoved(MouseEvent e) {
                int index = courseList.locationToIndex(e.getPoint());
                if (index != hoveredIndex) {
                    hoveredIndex = index;
                    courseList.repaint();
                }
            }
        });

        // Override again to handle hover
        courseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Course course = (Course) value;
                setText(course.getName());
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                Point mousePos = list.getMousePosition();

                if (!isSelected && mousePos != null && index == list.locationToIndex(mousePos)) {
                    setBackground(new Color(0xEBEBE9)); // Hover color
                    setForeground(new Color(0x2E2E2E));
                } else if (isSelected) {
                    setBackground(new Color(0xE3E2DF)); // Selected background
                    setForeground(Color.BLACK);
                } else {
                    setBackground(new Color(0xF7F6F3)); // Default background
                    setForeground(new Color(0x2E2E2E));
                }

                return this;
            }
        });

        courseList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    Course selectedCourse = courseList.getSelectedValue();
                    if (selectedCourse != null) {
                        mainFrame.showCourseDetail(selectedCourse.getId());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(0xF7F6F3));
        courseListPanel.add(scrollPane, BorderLayout.CENTER);
        add(courseListPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomPanel.setOpaque(false);

        JButton aboutButton = createSidebarButton("About Us");
        aboutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bottomPanel.add(aboutButton);

        JButton logOutButton = createSidebarButton("Log Out");
        logOutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logOutButton.addActionListener(e -> {
            mainFrame.saveDataInBackground();
            System.exit(0);
        });
        bottomPanel.add(logOutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshCourseList();
    }

    // Helper to create buttons with Notion style colors and font
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(new Color(0x2E2E2E));
        button.setBackground(new Color(0xF7F6F3));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setFocusPainted(false);
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0xEBEBE9));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0xF7F6F3));
            }
        });
        return button;
    }

    public void refreshCourseList() {
        courseListModel.clear();
        for (Course course : dataManager.getAllCourses()) {
            courseListModel.addElement(course);
        }
    }
}
