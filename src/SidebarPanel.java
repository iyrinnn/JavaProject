import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;


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
        setBackground(new Color(64, 46, 75)); // Dark purple

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false); // Make transparent to see parent's background

        JLabel userNameLabel = new JLabel("Irin Sultana & Sujana Farid");
        userNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userNameLabel.setForeground(Color.WHITE);
        headerPanel.add(userNameLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        JButton homeButton = new JButton("Home");
        homeButton.setForeground(Color.WHITE);
        homeButton.setBackground(new Color(84, 66, 95));
        homeButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        homeButton.addActionListener(e -> mainFrame.showDashboard());
        headerPanel.add(homeButton);
        headerPanel.add(Box.createVerticalStrut(5));

        JButton addCourseButton = new JButton("Add new course");
        addCourseButton.setForeground(Color.WHITE);
        addCourseButton.setBackground(new Color(84, 66, 95));
        addCourseButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
        courseTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        courseTitleLabel.setForeground(Color.WHITE);
        courseListPanel.add(courseTitleLabel, BorderLayout.NORTH);

        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseList.setFixedCellHeight(40);
        courseList.setBackground(new Color(84, 66, 95));
        courseList.setForeground(Color.WHITE);
        courseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Course course = (Course) value;
                setText(course.getName());
                setFont(getFont().deriveFont(14f));
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (isSelected) {
                    setBackground(new Color(104, 86, 115));
                } else {
                    setBackground(new Color(84, 66, 95));
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
        scrollPane.getViewport().setBackground(new Color(84, 66, 95));
        courseListPanel.add(scrollPane, BorderLayout.CENTER);
        add(courseListPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomPanel.setOpaque(false);

        JButton aboutButton = new JButton("About Us");
        aboutButton.setForeground(Color.WHITE);
        aboutButton.setBackground(new Color(104, 86, 115));
        aboutButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(aboutButton);

        JButton logOutButton = new JButton("Log Out");
        logOutButton.setForeground(Color.WHITE);
        logOutButton.setBackground(new Color(104, 86, 115));
        logOutButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logOutButton.addActionListener(e -> {
            mainFrame.saveDataInBackground();
            System.exit(0);
        });
        bottomPanel.add(logOutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshCourseList();
    }

    public void refreshCourseList() {
        courseListModel.clear();
        for (Course course : dataManager.getAllCourses()) {
            courseListModel.addElement(course);
        }
    }
}
