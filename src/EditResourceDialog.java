import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException; // Added for saveDataSafely
import java.util.UUID;

public class EditResourceDialog extends JDialog {
    private DataManager dataManager;
    private UUID topicId; 
    private Resource resourceToEdit;
    private TopicDetailPanel topicDetailPanel; //

    private JTextField titleField;
    private JComboBox<ResourceType> typeComboBox;
    private JTextField linksField;

    public EditResourceDialog(JFrame owner, DataManager dataManager, UUID topicId, Resource resourceToEdit, TopicDetailPanel topicDetailPanel) {
        super(owner, "Edit Resource", true);
        this.dataManager = dataManager;
        this.topicId = topicId; // Keep for context/future use if needed
        this.resourceToEdit = resourceToEdit;
        this.topicDetailPanel = topicDetailPanel;

        setSize(500, 280);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel titleLabel = new JLabel("Title:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(titleLabel, gbc);

        titleField = new JTextField(25);
        titleField.setText(resourceToEdit.getName()); // Pre-populate with current name
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);


        JLabel typeLabel = new JLabel("Type:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(typeLabel, gbc);

        typeComboBox = new JComboBox<>(ResourceType.values());
        typeComboBox.setSelectedItem(resourceToEdit.getType());
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(typeComboBox, gbc);


        JLabel linksLabel = new JLabel("Link:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(linksLabel, gbc);

        linksField = new JTextField(25);
        linksField.setText(resourceToEdit.getUrl()); // Pre-populate with current URL
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(linksField, gbc);

        add(formPanel, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton saveButton = new JButton("Save Resource");
        saveButton.addActionListener(e -> {
            String newTitle = titleField.getText().trim();
            ResourceType newType = (ResourceType) typeComboBox.getSelectedItem();
            String newLink = linksField.getText().trim();

            if (newTitle.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Resource title cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }


            resourceToEdit.setName(newTitle);
            resourceToEdit.setType(newType);
            resourceToEdit.setUrl(newLink);

            saveDataSafely();
            topicDetailPanel.refreshPanel();
            dispose(); // Close the dialog
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
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
