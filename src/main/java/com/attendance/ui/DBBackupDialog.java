package com.attendance.ui;

import com.attendance.util.DBBackupUtil;
import com.attendance.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Database Backup & Restore GUI Utility Dialog.
 */
public class DBBackupDialog extends JDialog {

    private JButton btnBackup;
    private JButton btnRestore;
    private JButton btnClose;
    private JLabel lblStatus;

    public DBBackupDialog(Frame parent) {
        super(parent, "Database Backup & Restore Tool", true);
        initUI();
    }

    private void initUI() {
        setSize(460, 320);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UIUtils.CARD_BG);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel("💾");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Database Maintenance");
        titleLabel.setFont(UIUtils.FONT_SUBTITLE);
        titleLabel.setForeground(UIUtils.TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(titleLabel);

        // Body Buttons
        JPanel bodyPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        bodyPanel.setOpaque(false);

        btnBackup = UIUtils.createButton("📦 Export Database Backup (.sql)", UIUtils.PRIMARY, Color.WHITE);
        btnRestore = UIUtils.createButton("📥 Restore Database from Script (.sql)", UIUtils.WARNING, Color.WHITE);

        bodyPanel.add(btnBackup);
        bodyPanel.add(btnRestore);

        lblStatus = new JLabel("Select an operation above.", SwingConstants.CENTER);
        lblStatus.setFont(UIUtils.FONT_REGULAR);
        lblStatus.setForeground(UIUtils.TEXT_MUTED);

        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setOpaque(false);
        centerContainer.add(bodyPanel, BorderLayout.CENTER);
        centerContainer.add(lblStatus, BorderLayout.SOUTH);

        // Bottom Bar
        btnClose = UIUtils.createButton("Close", UIUtils.SECONDARY, Color.WHITE);
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);
        bottomBar.add(btnClose);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerContainer, BorderLayout.CENTER);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        add(mainPanel);

        // Handlers
        btnClose.addActionListener(e -> dispose());
        btnBackup.addActionListener(e -> performBackup());
        btnRestore.addActionListener(e -> performRestore());
    }

    private void performBackup() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Database Backup File");
        String defaultName = "attendance_backup_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".sql";
        fileChooser.setSelectedFile(new File(defaultName));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".sql")) {
                file = new File(file.getAbsolutePath() + ".sql");
            }

            lblStatus.setText("Generating SQL backup script...");
            File finalFile = file;

            SwingUtilities.invokeLater(() -> {
                boolean success = DBBackupUtil.backupDatabase(finalFile);
                if (success) {
                    lblStatus.setText("Backup completed successfully.");
                    UIUtils.showInfoDialog(this, "Database backed up to:\n" + finalFile.getAbsolutePath(), "Backup Success");
                } else {
                    lblStatus.setText("Backup failed.");
                    UIUtils.showErrorDialog(this, "Failed to create database backup.", "Error");
                }
            });
        }
    }

    private void performRestore() {
        if (!UIUtils.showConfirmDialog(this, "WARNING: Restoring database will overwrite existing tables.\nDo you want to proceed?", "Confirm Database Restore")) {
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select SQL Backup File to Restore");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File scriptFile = fileChooser.getSelectedFile();
            lblStatus.setText("Restoring database from script...");

            SwingUtilities.invokeLater(() -> {
                boolean success = DBBackupUtil.restoreDatabase(scriptFile);
                if (success) {
                    lblStatus.setText("Database restored successfully.");
                    UIUtils.showInfoDialog(this, "Database restored successfully from file!", "Restore Success");
                } else {
                    lblStatus.setText("Restore failed.");
                    UIUtils.showErrorDialog(this, "Failed to restore database from script.", "Error");
                }
            });
        }
    }
}
