import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddCourseDialog extends JDialog {
    private DataManager dataManager;
    private SidebarPanel sidebarPanel;
    private MainApplicationFrame mainFrame;

    public AddCourseDialog(JFrame owner, DataManager dataManager, SidebarPanel sidebarPanel) {
        super(owner, "Add new course", true);
        this.dataManager = dataManager;
        this.sidebarPanel = sidebarPanel;
        this.mainFrame = (MainApplicationFrame) owner;

        setSize(500, 300); // increased size
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(30, 30, 20, 30)); // more padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // more spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Course Title");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));  // bigger font for label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0;
        formPanel.add(titleLabel, gbc);

        JTextField titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.BOLD, 14));  // readable font for input
        titleField.setPreferredSize(new Dimension(300, 35));  // wider and taller text field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(100, 40));
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
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
