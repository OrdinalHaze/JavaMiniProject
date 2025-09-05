package org.example;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Setup FlatLaf (dark theme)
        try {
            FlatDarkLaf.setup();
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new AuthUI().setVisible(true));
    }
}

