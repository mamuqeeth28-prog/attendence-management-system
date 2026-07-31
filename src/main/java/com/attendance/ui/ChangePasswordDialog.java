package com.attendance.ui;

import com.attendance.dao.AdminDAO;
import com.attendance.model.Admin;
import com.attendance.util.PasswordUtil;
import com.attendance.util.UIUtils;
import com.attendance.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Admin Profile Update & Change Password Dialog.
 */
public class ChangePasswordDialog extends JDialog {

    private final Admin admin;
    private final AdminDAO adminDAO = new AdminDAO();

    private JTextField txtFullName;
    private JTextField txtEmail;

    private JPasswordField txtOldPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    private JButton btnSaveProfile;
    private JButton btnChangePassword;
    private JButton btnClose;

    public ChangePasswordDialog(Frame parent, Admin admin) {
        super(parent, "Admin Account & Security Settings", true);
        this.admin = admin;
        initUI();
    }

    private void initUI() {
        setSize(460, 480);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UIUtils.CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_BOLD);

        // Tab 1: Profile Settings
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(UIUtils.CARD_BG);
        profilePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        txtFullName = new JTextField(admin.getFullName(), 20);
        txtFullName.setFont(UIUtils.FONT_REGULAR);
        txtEmail = new JTextField(admin.getEmail(), 20);
        txtEmail.setFont(UIUtils.FONT_REGULAR);

        btnSaveProfile = UIUtils.createButton("Update Profile", UIUtils.PRIMARY, Color.WHITE);

        int r = 0;
        profilePanel.add(new JLabel("Username:"), getGbc(0, r));
        JLabel lblUser = new JLabel(admin.getUsername());
        lblUser.setFont(UIUtils.FONT_BOLD);
        profilePanel.add(lblUser, getGbc(1, r++));

        profilePanel.add(new JLabel("Full Name:"), getGbc(0, r));
        profilePanel.add(txtFullName, getGbc(1, r++));

        profilePanel.add(new JLabel("Email Address:"), getGbc(0, r));
        profilePanel.add(txtEmail, getGbc(1, r++));

        GridBagConstraints gbcProf = getGbc(1, r);
        gbcProf.anchor = GridBagConstraints.EAST;
        profilePanel.add(btnSaveProfile, gbcProf);

        // Tab 2: Change Password
        JPanel pwdPanel = new JPanel(new GridBagLayout());
        pwdPanel.setBackground(UIUtils.CARD_BG);
        pwdPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        txtOldPassword = new JPasswordField(20);
        txtNewPassword = new JPasswordField(20);
        txtConfirmPassword = new JPasswordField(20);

        btnChangePassword = UIUtils.createButton("Change Password", UIUtils.DANGER, Color.WHITE);

        r = 0;
        pwdPanel.add(new JLabel("Current Password:"), getGbc(0, r));
        pwdPanel.add(txtOldPassword, getGbc(1, r++));

        pwdPanel.add(new JLabel("New Password:"), getGbc(0, r));
        pwdPanel.add(txtNewPassword, getGbc(1, r++));

        pwdPanel.add(new JLabel("Confirm New Password:"), getGbc(0, r));
        pwdPanel.add(txtConfirmPassword, getGbc(1, r++));

        GridBagConstraints gbcPwd = getGbc(1, r);
        gbcPwd.anchor = GridBagConstraints.EAST;
        pwdPanel.add(btnChangePassword, gbcPwd);

        tabbedPane.addTab("👤 Profile", profilePanel);
        tabbedPane.addTab("🔒 Change Password", pwdPanel);

        // Bottom Close Button
        btnClose = UIUtils.createButton("Close", UIUtils.SECONDARY, Color.WHITE);
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);
        bottomBar.add(btnClose);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Listeners
        btnClose.addActionListener(e -> dispose());
        btnSaveProfile.addActionListener(e -> saveProfile());
        btnChangePassword.addActionListener(e -> changePassword());
    }

    private void saveProfile() {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();

        if (ValidationUtil.isEmpty(fullName)) {
            UIUtils.showErrorDialog(this, "Full Name cannot be empty.", "Validation Error");
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            UIUtils.showErrorDialog(this, "Invalid email address format.", "Validation Error");
            return;
        }

        boolean ok = adminDAO.updateProfile(admin.getId(), fullName, email);
        if (ok) {
            admin.setFullName(fullName);
            admin.setEmail(email);
            UIUtils.showInfoDialog(this, "Profile updated successfully!", "Success");
        } else {
            UIUtils.showErrorDialog(this, "Failed to update profile.", "Database Error");
        }
    }

    private void changePassword() {
        String oldPass = new String(txtOldPassword.getPassword());
        String newPass = new String(txtNewPassword.getPassword());
        String confirmPass = new String(txtConfirmPassword.getPassword());

        if (ValidationUtil.isEmpty(oldPass) || ValidationUtil.isEmpty(newPass) || ValidationUtil.isEmpty(confirmPass)) {
            UIUtils.showErrorDialog(this, "Please fill in all password fields.", "Validation Error");
            return;
        }

        if (!PasswordUtil.verifyPassword(oldPass, admin.getPasswordHash())) {
            UIUtils.showErrorDialog(this, "Current password is incorrect!", "Authentication Error");
            return;
        }

        if (newPass.length() < 4) {
            UIUtils.showErrorDialog(this, "New password must be at least 4 characters long.", "Validation Error");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            UIUtils.showErrorDialog(this, "New password and Confirm password do not match!", "Validation Error");
            return;
        }

        boolean ok = adminDAO.updatePassword(admin.getId(), newPass);
        if (ok) {
            admin.setPasswordHash(PasswordUtil.hashPassword(newPass));
            UIUtils.showInfoDialog(this, "Password changed successfully!", "Success");
            txtOldPassword.setText("");
            txtNewPassword.setText("");
            txtConfirmPassword.setText("");
        } else {
            UIUtils.showErrorDialog(this, "Failed to update password.", "Database Error");
        }
    }

    private GridBagConstraints getGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        if (x == 1) gbc.weightx = 1.0;
        return gbc;
    }
}
