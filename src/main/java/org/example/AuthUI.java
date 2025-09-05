package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.security.SecureRandom;

public class AuthUI extends JFrame {
    private final UserDAO userDAO = new UserDAO();

    public AuthUI() {
        setTitle("Secure Journal - Login / Register");
        setSize(480, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Login Panel ---
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField loginUserField = new JTextField();
        JPasswordField loginPassField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        gc.gridx = 0; gc.gridy = 0; loginPanel.add(new JLabel("Username:"), gc);
        gc.gridx = 1; gc.gridy = 0; loginPanel.add(loginUserField, gc);
        gc.gridx = 0; gc.gridy = 1; loginPanel.add(new JLabel("Password:"), gc);
        gc.gridx = 1; gc.gridy = 1; loginPanel.add(loginPassField, gc);
        gc.gridx = 1; gc.gridy = 2; loginPanel.add(loginButton, gc);

        loginButton.addActionListener(e -> {
            String username = loginUserField.getText().trim();
            String password = new String(loginPassField.getPassword());
            if (userDAO.login(username, password)) {
                int uid = userDAO.getUserId(username);
                SwingUtilities.invokeLater(() -> {
                    new JournalUI(uid, username).setVisible(true);
                });
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("🔑 Login", loginPanel);

        // --- Register Panel ---
        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints rg = new GridBagConstraints();
        rg.insets = new Insets(6, 6, 6, 6);
        rg.fill = GridBagConstraints.HORIZONTAL;

        JTextField regUserField = new JTextField();
        JPasswordField regPassField = new JPasswordField();
        JButton regButton = new JButton("Register");

        JButton generateButton = new JButton("Generate Password");
        JTextField generatedField = new JTextField();
        generatedField.setEditable(false);
        JButton copyButton = new JButton("Copy");

        rg.gridx = 0; rg.gridy = 0; registerPanel.add(new JLabel("Username:"), rg);
        rg.gridx = 1; rg.gridy = 0; registerPanel.add(regUserField, rg);
        rg.gridx = 0; rg.gridy = 1; registerPanel.add(new JLabel("Password:"), rg);
        rg.gridx = 1; rg.gridy = 1; registerPanel.add(regPassField, rg);
        rg.gridx = 0; rg.gridy = 2; registerPanel.add(generateButton, rg);
        rg.gridx = 1; rg.gridy = 2; registerPanel.add(generatedField, rg);
        rg.gridx = 2; rg.gridy = 2; registerPanel.add(copyButton, rg);
        rg.gridx = 1; rg.gridy = 3; registerPanel.add(regButton, rg);

        generateButton.addActionListener(e -> {
            String pwd = generateStrongPassword(14);
            generatedField.setText(pwd);
            regPassField.setText(pwd);
        });

        copyButton.addActionListener(e -> {
            String text = generatedField.getText();
            if (text != null && !text.isBlank()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
                JOptionPane.showMessageDialog(this, "Password copied to clipboard");
            }
        });

        regButton.addActionListener(e -> {
            String username = regUserField.getText().trim();
            String password = new String(regPassField.getPassword());
            if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (userDAO.register(username, password)) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed (username may already exist).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("📝 Register", registerPanel);

        add(tabbedPane);
    }

    private String generateStrongPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*()-_=+";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
