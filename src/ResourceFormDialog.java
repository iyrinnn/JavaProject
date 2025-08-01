// ResourceFormDialog.java
// This is a modal dialog for adding a new resource to a topic.

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.UUID;

public class ResourceFormDialog extends JDialog {
    private DataManager dataManager;
    private UUID courseId;
    private TopicDetailPanel topicDetailPanel;

    public ResourceFormDialog(JFrame owner, DataManager dataManager, UUID courseId, TopicDetailPanel topicDetailPanel) {
        super(owner, "Add new resource", true);
        this.dataManager = dataManager;
        this.courseId = courseId;
        this.topicDetailPanel = topicDetailPanel;

        setSize(500, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Title");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);

        JTextField titleField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        JLabel descriptionLabel = new JLabel("Description");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(descriptionLabel, gbc);

        JTextArea descriptionArea = new JTextArea(3, 25);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(descriptionScrollPane, gbc);

        JLabel typeLabel = new JLabel("Type");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(typeLabel, gbc);

        JComboBox<ResourceType> typeComboBox = new JComboBox<>(ResourceType.values());
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(typeComboBox, gbc);

        JLabel linksLabel = new JLabel("Links");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(linksLabel, gbc);

        JTextField linksField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(linksField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton saveButton = new JButton("Save resource");
        saveButton.addActionListener(e -> {
            String title = titleField.getText();
            String description = descriptionArea.getText();
            ResourceType type = (ResourceType) typeComboBox.getSelectedItem();
            String link = linksField.getText();

            if (title != null && !title.trim().isEmpty()) {
                Resource newResource = new Resource(courseId, topicDetailPanel.currentTopic.getId(), title, description, type, link);
                dataManager.addResourceToTopic(courseId, topicDetailPanel.currentTopic.getId(), newResource);
                topicDetailPanel.refreshPanel();
                ((MainApplicationFrame) owner).saveDataInBackground();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Resource title cannot be empty.");
            }
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
