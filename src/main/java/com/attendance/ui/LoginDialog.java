package com.attendance.ui;

import com.attendance.dao.AdminDAO;
import com.attendance.database.DatabaseConnection;
import com.attendance.database.DatabaseInitializer;
import com.attendance.model.Admin;
import com.attendance.util.UIUtils;
import com.attendance.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Authentication dialog for Admin login and Database configuration.
 */
public class LoginDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JButton btnDbConfig;
    private JLabel lblStatus;

    private Admin loggedInAdmin = null;
    private final AdminDAO adminDAO = new AdminDAO();

    public LoginDialog(Frame parent) {
        super(parent, "Attendance System - Admin Login", true);
        initUI();
    }

    private void initUI() {
        setSize(420, 460);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UIUtils.CARD_BG);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(UIUtils.FONT_TITLE);
        titleLabel.setForeground(UIUtils.TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Log in to manage attendance");
        subtitleLabel.setFont(UIUtils.FONT_REGULAR);
        subtitleLabel.setForeground(UIUtils.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitleLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        // Username
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(UIUtils.FONT_BOLD);
        lblUsername.setForeground(UIUtils.TEXT_DARK);

        txtUsername = new JTextField(20);
        txtUsername.setFont(UIUtils.FONT_REGULAR);
        txtUsername.setPreferredSize(new Dimension(0, 36));

        // Password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(UIUtils.FONT_BOLD);
        lblPassword.setForeground(UIUtils.TEXT_DARK);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(UIUtils.FONT_REGULAR);
        txtPassword.setPreferredSize(new Dimension(0, 36));

        gbc.gridy = 0; formPanel.add(lblUsername, gbc);
        gbc.gridy = 1; formPanel.add(txtUsername, gbc);
        gbc.gridy = 2; formPanel.add(lblPassword, gbc);
        gbc.gridy = 3; formPanel.add(txtPassword, gbc);

        // Status Label
        lblStatus = new JLabel(" ");
        lblStatus.setFont(UIUtils.FONT_REGULAR);
        lblStatus.setForeground(UIUtils.DANGER);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4; formPanel.add(lblStatus, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setOpaque(false);

        btnLogin = UIUtils.createButton("LOGIN", UIUtils.PRIMARY, Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(0, 40));

        JPanel subBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        subBtnPanel.setOpaque(false);

        btnCancel = UIUtils.createButton("Exit", UIUtils.SECONDARY, Color.WHITE);
        btnDbConfig = UIUtils.createButton("DB Settings", new Color(100, 116, 139), Color.WHITE);

        subBtnPanel.add(btnDbConfig);
        subBtnPanel.add(btnCancel);

        buttonPanel.add(btnLogin);
        buttonPanel.add(subBtnPanel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Listeners
        btnLogin.addActionListener(e -> performLogin());
        btnCancel.addActionListener(e -> System.exit(0));
        btnDbConfig.addActionListener(e -> openDbConfigDialog());

        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };

        txtUsername.addKeyListener(enterKeyListener);
        txtPassword.addKeyListener(enterKeyListener);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)) {
            lblStatus.setText("Please enter username and password!");
            return;
        }

        lblStatus.setText("Authenticating...");
        btnLogin.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            Admin admin = adminDAO.authenticate(username, password);
            if (admin != null) {
                this.loggedInAdmin = admin;
                dispose();
            } else {
                lblStatus.setText("Invalid username or password!");
                btnLogin.setEnabled(true);
            }
        });
    }

    private void openDbConfigDialog() {
        JTextField txtHost = new JTextField("localhost", 15);
        JTextField txtPort = new JTextField("3306", 15);
        JTextField txtDb = new JTextField("attendance_db", 15);
        JTextField txtUser = new JTextField("root", 15);
        JPasswordField txtPass = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Host:")); panel.add(txtHost);
        panel.add(new JLabel("Port:")); panel.add(txtPort);
        panel.add(new JLabel("Database:")); panel.add(txtDb);
        panel.add(new JLabel("Username:")); panel.add(txtUser);
        panel.add(new JLabel("Password:")); panel.add(txtPass);

        int result = JOptionPane.showConfirmDialog(this, panel, "MySQL Connection Settings",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            DatabaseConnection.setCredentials(
                    txtHost.getText().trim(),
                    txtPort.getText().trim(),
                    txtDb.getText().trim(),
                    txtUser.getText().trim(),
                    new String(txtPass.getPassword())
            );
            if (DatabaseInitializer.initializeDatabase()) {
                UIUtils.showInfoDialog(this, "Connected to MySQL successfully!", "Database Success");
            } else {
                UIUtils.showErrorDialog(this, "Could not connect with provided settings.", "Connection Failed");
            }
        }
    }

    public Admin getLoggedInAdmin() {
        return loggedInAdmin;
    }
}
