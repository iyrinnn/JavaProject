import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class SidebarPanel extends JPanel {
    private final DataManager dataManager;
    private final MainApplicationFrame mainFrame;
    private final DefaultListModel<Course> courseListModel;
    private final JList<Course> courseList;

    public SidebarPanel(DataManager dataManager, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(0xF7F6F3));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)), // top, left, bottom, right
                new EmptyBorder(10, 10, 10, 10) // internal padding
        ));




        // === Top Panel (User + Buttons) ===
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel userNameLabel = new JLabel("Let go of me");
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userNameLabel.setForeground(new Color(0x2E2E2E));
        headerPanel.add(userNameLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        JButton homeButton = createSidebarButton("Home");
        homeButton.addActionListener(e -> mainFrame.showDashboard());
        headerPanel.add(homeButton);
        headerPanel.add(Box.createVerticalStrut(5));

        JButton addCourseButton = createSidebarButton("Add new course");
        addCourseButton.addActionListener(e -> {
            AddCourseDialog dialog = new AddCourseDialog(mainFrame, dataManager, this);
            dialog.setVisible(true);
        });
        headerPanel.add(addCourseButton);
        headerPanel.add(Box.createVerticalStrut(20));

        add(headerPanel, BorderLayout.NORTH);

        // === Center: Course List ===
        JPanel courseListPanel = new JPanel(new BorderLayout());
        courseListPanel.setOpaque(false);

        JLabel courseTitleLabel = new JLabel("Courses");
        courseTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        courseTitleLabel.setForeground(new Color(0x2E2E2E));
        courseListPanel.add(courseTitleLabel, BorderLayout.NORTH);

        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseList.setFixedCellHeight(40);
        courseList.setBackground(new Color(0xF7F6F3));
        courseList.setForeground(new Color(0x2E2E2E));
        courseList.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Single unified custom renderer
        courseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Course course = (Course) value;
                setText(course.getName());
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                Point mousePos = list.getMousePosition();
                boolean isHovered = mousePos != null && index == list.locationToIndex(mousePos);

                if (isSelected) {
                    setBackground(new Color(0xE3E2DF));
                    setForeground(Color.BLACK);
                } else if (isHovered) {
                    setBackground(new Color(0xEBEBE9));
                    setForeground(new Color(0x2E2E2E));
                } else {
                    setBackground(new Color(0xF7F6F3));
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
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        courseListPanel.add(scrollPane, BorderLayout.CENTER);

        add(courseListPanel, BorderLayout.CENTER);

        // === Bottom Panel: About + Logout ===
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(0xF7F6F3));
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        bottomPanel.add(createSidebarItem("ℹ️", "About", () -> {
            JOptionPane.showMessageDialog(mainFrame, "Course Management App\nBuilt by Irin", "About", JOptionPane.INFORMATION_MESSAGE);
        }));
        bottomPanel.add(Box.createVerticalStrut(5));

        bottomPanel.add(createSidebarItem("🚪", "Logout", () -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        }));

        add(bottomPanel, BorderLayout.SOUTH);

        refreshCourseList();
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(0xEDEDED));
        button.setForeground(new Color(0x2E2E2E));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private JPanel createSidebarItem(String icon, String text, Runnable action) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(new Color(0xF7F6F3));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setOpaque(true);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setForeground(new Color(0x2E2E2E));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        textLabel.setForeground(new Color(0x2E2E2E));

        panel.add(iconLabel);
        panel.add(textLabel);

        panel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(0xEBEBE9));
            }

            public void mouseExited(MouseEvent e) {
                panel.setBackground(new Color(0xF7F6F3));
            }

            public void mouseClicked(MouseEvent e) {
                if (action != null) action.run();
            }
        });

        return panel;
    }

    public void refreshCourseList() {
        courseListModel.clear();
        for (Course course : dataManager.getAllCourses()) {
            courseListModel.addElement(course);
        }
    }
}



