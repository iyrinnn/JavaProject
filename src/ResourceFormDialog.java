import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.UUID;

public class ResourceFormDialog extends JDialog {
    private DataManager dataManager;
    private UUID courseId;
    private TopicDetailPanel topicDetailPanel;

    public ResourceFormDialog(JFrame owner, DataManager dataManager, UUID courseId, TopicDetailPanel topicDetailPanel) {
        super(owner, "Add New Resource", true);
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

        // Title label and text field
        JLabel titleLabel = new JLabel("Title:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(titleLabel, gbc);

        JTextField titleField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        // Description label and text area
        JLabel descriptionLabel = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(descriptionLabel, gbc);

        JTextArea descriptionArea = new JTextArea(3, 25);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(descriptionScrollPane, gbc);

        // Type label and combo box
        JLabel typeLabel = new JLabel("Type:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(typeLabel, gbc);

        JComboBox<ResourceType> typeComboBox = new JComboBox<>(ResourceType.values());
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(typeComboBox, gbc);

        // Links label and text field
        JLabel linksLabel = new JLabel("Links:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(linksLabel, gbc);

        JTextField linksField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(linksField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons panel with Save and Cancel buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton saveButton = new JButton("Save Resource");
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            ResourceType type = (ResourceType) typeComboBox.getSelectedItem();
            String link = linksField.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Resource title cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create new resource with topic's ID from topicDetailPanel
            Resource newResource = new Resource(courseId, topicDetailPanel.getCurrentTopic().getId(), title, description, type, link);
            dataManager.addResourceToTopic(courseId, topicDetailPanel.getCurrentTopic().getId(), newResource);

            topicDetailPanel.refreshPanel();
            if (owner instanceof MainApplicationFrame) {
                ((MainApplicationFrame) owner).saveDataInBackground();
            }
            dispose();
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
