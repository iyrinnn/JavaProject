import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Register extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public Register() {
        setTitle("Register - Smart Revision Organizer");
        setSize(800, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // -------- LEFT IMAGE PANEL --------
        JLabel imageLabel = new JLabel();
        try {
            ImageIcon imageIcon = new ImageIcon("login.jpg");
            Image image = imageIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            // Fallback if image not found
            imageLabel.setText("Smart Revision Organizer");
            imageLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setBackground(new Color(76, 175, 80));
            imageLabel.setForeground(Color.WHITE);
            imageLabel.setOpaque(true);
        }

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // -------- RIGHT FORM PANEL --------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Register");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(title, gbc);

        gbc.gridwidth = 1;

        // Username Label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(userLabel, gbc);

        // Username Field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        usernameField.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Password Label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        passwordField.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Confirm Password Label
        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(confirmLabel, gbc);

        // Confirm Password Field
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        confirmPasswordField.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Register Button
        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        registerBtn.setBackground(new Color(76, 175, 80));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setPreferredSize(new Dimension(120, 45));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(registerBtn, gbc);

        // Back to Login Button
        JButton loginBtn = new JButton("Back to Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginBtn.setBackground(new Color(70, 130, 180));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(160, 45));
        gbc.gridx = 1;
        formPanel.add(loginBtn, gbc);

        // Instructions
        JLabel instructionLabel = new JLabel("Create a new account");
        instructionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 15, 15, 15);
        formPanel.add(instructionLabel, gbc);

        // -------- SPLIT PANE --------
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imagePanel, formPanel);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);

        add(splitPane);

        // -------- BUTTON ACTIONS --------
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister(e);
            }
        });

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Login();
            }
        });

        // Enter key support
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister(e);
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmPasswordField.requestFocus();
            }
        });

        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordField.requestFocus();
            }
        });

        setVisible(true);
    }

    private void handleRegister(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this, "Username must be at least 3 characters long.");
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        if (UserManager.isUserExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Choose a different username.");
            return;
        }

        if (UserManager.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
            dispose();
            new Login();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
        }
    }


}