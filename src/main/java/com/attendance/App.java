package com.attendance;

import com.formdev.flatlaf.FlatLightLaf;
import com.attendance.database.DatabaseInitializer;
import com.attendance.model.Admin;
import com.attendance.ui.LoginDialog;
import com.attendance.ui.MainFrame;

import javax.swing.*;

/**
 * Main Application Launcher for Attendance Management System.
 */
public class App {

    public static void main(String[] args) {
        // Apply FlatLaf modern UI look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf look and feel. Falling back to system default.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // Initialize GUI in Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Initialize Database Schema if necessary
            DatabaseInitializer.initializeDatabase();

            // Open Login Dialog
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);

            Admin admin = loginDialog.getLoggedInAdmin();
            if (admin != null) {
                MainFrame mainFrame = new MainFrame(admin);
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
