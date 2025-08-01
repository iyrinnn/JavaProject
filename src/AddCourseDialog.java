// AddCourseDialog.java
// This is a modal dialog for adding a new course.

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;

public class AddCourseDialog extends JDialog {
    private DataManager dataManager;
    private SidebarPanel sidebarPanel;
    private MainApplicationFrame mainFrame;

    public AddCourseDialog(JFrame owner, DataManager dataManager, SidebarPanel sidebarPanel) {
        super(owner, "Add new course", true);
        this.dataManager = dataManager;
        this.sidebarPanel = sidebarPanel;
        this.mainFrame = (MainApplicationFrame) owner;

        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Title");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(titleLabel, gbc);

        JTextField titleField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String courseName = titleField.getText();
            if (courseName != null && !courseName.trim().isEmpty()) {
                Course newCourse = new Course(courseName);
                dataManager.addCourse(newCourse);
                sidebarPanel.refreshCourseList();
                mainFrame.showCourseDetail(newCourse.getId());
                mainFrame.saveDataInBackground();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a course name.");
            }
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
