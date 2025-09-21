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
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),  // thin border all around
                new EmptyBorder(10, 10, 10, 10)  // padding inside
        ));

        setBackground(new Color(0xF7F6F3)); // Notion sidebar background (off-white)

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false); // Transparent to show parent's background

        JLabel userNameLabel = new JLabel("Study Buddy");
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Notion uses 14pt bold for sidebar user label
        userNameLabel.setForeground(new Color(0x2E2E2E)); // Notion dark gray text
        headerPanel.add(userNameLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        // Use light gray buttons with subtle hover effect
        JButton homeButton = createSidebarButton("Home");   // Home Button
        homeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Bold 14pt for buttons
        homeButton.addActionListener(e -> mainFrame.showDashboard());
        headerPanel.add(homeButton);
        headerPanel.add(Box.createVerticalStrut(5));

        JButton addCourseButton = createSidebarButton("Add new course"); // Add course button
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

        // ==== Collapsible toggle panel for "Courses" ====
        JPanel coursesTogglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        coursesTogglePanel.setBackground(new Color(0xF7F6F3));
        coursesTogglePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel arrowLabel = new JLabel("\u25BC"); // ▼ down arrow = expanded
        arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        arrowLabel.setForeground(new Color(0x2E2E2E));

        JLabel titleLabel = new JLabel("Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0x2E2E2E));

        coursesTogglePanel.add(arrowLabel);
        coursesTogglePanel.add(titleLabel);

        // Container for course list scroll pane
        JPanel coursesListContainer = new JPanel(new BorderLayout());
        coursesListContainer.setOpaque(false);

        // Create course list and scroll pane as before
        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseList.setFixedCellHeight(40);
        courseList.setBackground(new Color(0xF7F6F3)); // Notion sidebar background
        courseList.setForeground(new Color(0x2E2E2E)); // Dark gray text
        courseList.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // List items regular 14pt

        // Custom renderer for selected and hovered states
        courseList.setCellRenderer(new DefaultListCellRenderer() {
            private int hoveredIndex = -1;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Course course = (Course) value;
                setText(course.getName());
                setFont(new Font("Segoe UI", isSelected ? Font.BOLD : Font.PLAIN, 14));
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

        JScrollPane scrollPane = new JScrollPane(courseList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(0xF7F6F3));
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(240, 240, 240)));

        coursesListContainer.add(scrollPane, BorderLayout.CENTER);

        courseListPanel.add(coursesTogglePanel, BorderLayout.NORTH);
        courseListPanel.add(coursesListContainer, BorderLayout.CENTER);

        // Toggle logic for collapse/expand
        coursesTogglePanel.addMouseListener(new MouseAdapter() {
            private boolean expanded = true;

            @Override
            public void mouseClicked(MouseEvent e) {
                expanded = !expanded;
                coursesListContainer.setVisible(expanded);
                arrowLabel.setText(expanded ? "\u25BC" : "\u25B6"); // ▼ down or ▶ right
                courseListPanel.revalidate();
                courseListPanel.repaint();
            }
        });

        add(courseListPanel, BorderLayout.CENTER);

        // Bottom panel with About and Logout buttons stacked vertically
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(0xF7F6F3));
        bottomPanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        bottomPanel.add(createSidebarItem("ℹ️", "About", () -> {
            JOptionPane.showMessageDialog(mainFrame, "Course Management Application\nDeveloped by Irin.", "About", JOptionPane.INFORMATION_MESSAGE);
        }));
        bottomPanel.add(Box.createVerticalStrut(5));

        bottomPanel.add(createSidebarItem("🚪", "Logout", () -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }));

        add(bottomPanel, BorderLayout.SOUTH);

        refreshCourseList();
    }

    // Helper to create buttons with Notion style colors and font
    // Modified helper to use emoji icon + label in JPanel instead of JButton
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

        // Hover effect
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
