package com.attendance.ui;

import com.attendance.model.Admin;
import com.attendance.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main Window containing Sidebar Navigation, Top Header, and CardLayout View Switcher.
 */
public class MainFrame extends JFrame {

    private final Admin admin;

    private JPanel cardContainer;
    private CardLayout cardLayout;

    private DashboardPanel dashboardPanel;
    private StudentManagementPanel studentPanel;
    private AttendancePanel attendancePanel;
    private ReportsPanel reportsPanel;

    private JLabel lblHeaderAdmin;
    private JLabel lblClock;

    private JButton btnNavDashboard;
    private JButton btnNavStudents;
    private JButton btnNavAttendance;
    private JButton btnNavReports;

    public MainFrame(Admin admin) {
        this.admin = admin;
        initUI();
    }

    private void initUI() {
        setTitle("Attendance Management System - Logged in as " + admin.getFullName());
        setSize(1280, 780);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UIUtils.BG_LIGHT);

        // Sidebar Navigation
        JPanel sidebar = createSidebar();

        // Top Bar
        JPanel topBar = createTopBar();

        // Content Area (CardLayout)
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        dashboardPanel = new DashboardPanel(this);
        studentPanel = new StudentManagementPanel();
        attendancePanel = new AttendancePanel();
        reportsPanel = new ReportsPanel();

        cardContainer.add(dashboardPanel, "DASHBOARD");
        cardContainer.add(studentPanel, "STUDENTS");
        cardContainer.add(attendancePanel, "ATTENDANCE");
        cardContainer.add(reportsPanel, "REPORTS");

        // Status Bar at Bottom
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(241, 245, 249));
        statusBar.setBorder(new EmptyBorder(6, 15, 6, 15));

        JLabel lblDbStatus = new JLabel("🟢 Connected to MySQL Database (attendance_db)");
        lblDbStatus.setFont(UIUtils.FONT_REGULAR);
        lblDbStatus.setForeground(UIUtils.SECONDARY);

        lblClock = new JLabel();
        lblClock.setFont(UIUtils.FONT_REGULAR);
        lblClock.setForeground(UIUtils.SECONDARY);
        startClockTimer();

        statusBar.add(lblDbStatus, BorderLayout.WEST);
        statusBar.add(lblClock, BorderLayout.EAST);

        // Layout Assembly
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topBar, BorderLayout.NORTH);
        centerPanel.add(cardContainer, BorderLayout.CENTER);
        centerPanel.add(statusBar, BorderLayout.SOUTH);

        rootPanel.add(sidebar, BorderLayout.WEST);
        rootPanel.add(centerPanel, BorderLayout.CENTER);

        add(rootPanel);

        // Default view
        showPanel("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(15, 23, 42)); // Dark Slate/Navy
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        // Brand / Logo Header
        JLabel logoIcon = new JLabel("🎓 ATTENDANCE");
        logoIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoIcon.setForeground(Color.WHITE);
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoSub = new JLabel("System Management v1.0");
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoSub.setForeground(new Color(148, 163, 184));
        logoSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logoIcon);
        sidebar.add(logoSub);
        sidebar.add(Box.createVerticalStrut(30));

        // Nav Buttons
        btnNavDashboard = createNavButton("📊 Dashboard", "DASHBOARD");
        btnNavStudents = createNavButton("👨‍🎓 Students", "STUDENTS");
        btnNavAttendance = createNavButton("📝 Attendance", "ATTENDANCE");
        btnNavReports = createNavButton("📈 Reports", "REPORTS");

        sidebar.add(btnNavDashboard);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnNavStudents);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnNavAttendance);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnNavReports);

        sidebar.add(Box.createVerticalGlue());

        // Tools Section
        JLabel lblTools = new JLabel("SYSTEM TOOLS");
        lblTools.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTools.setForeground(new Color(148, 163, 184));
        lblTools.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnBackup = createNavButton("💾 DB Backup", null);
        btnBackup.addActionListener(e -> new DBBackupDialog(this).setVisible(true));

        JButton btnSettings = createNavButton("⚙️ Account Settings", null);
        btnSettings.addActionListener(e -> {
            new ChangePasswordDialog(this, admin).setVisible(true);
            lblHeaderAdmin.setText("👤 " + admin.getFullName());
        });

        JButton btnLogout = createNavButton("🚪 Logout", null);
        btnLogout.setBackground(new Color(220, 38, 38));
        btnLogout.addActionListener(e -> logout());

        sidebar.add(lblTools);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnBackup);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnSettings);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(UIUtils.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        if (cardName != null) {
            btn.addActionListener(e -> showPanel(cardName));
        }
        return btn;
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(12, 25, 12, 25)
        ));

        JLabel title = new JLabel("Attendance Management Workspace");
        title.setFont(UIUtils.FONT_SUBTITLE);
        title.setForeground(UIUtils.TEXT_DARK);

        JPanel rightUserPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightUserPanel.setOpaque(false);

        lblHeaderAdmin = new JLabel("👤 " + admin.getFullName());
        lblHeaderAdmin.setFont(UIUtils.FONT_BOLD);
        lblHeaderAdmin.setForeground(UIUtils.PRIMARY);

        rightUserPanel.add(lblHeaderAdmin);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightUserPanel, BorderLayout.EAST);

        return topBar;
    }

    public void showPanel(String name) {
        cardLayout.show(cardContainer, name);
        resetNavButtonStyles();

        if ("DASHBOARD".equals(name)) {
            btnNavDashboard.setBackground(UIUtils.PRIMARY);
            dashboardPanel.refreshStats();
        } else if ("STUDENTS".equals(name)) {
            btnNavStudents.setBackground(UIUtils.PRIMARY);
            studentPanel.loadStudentData();
        } else if ("ATTENDANCE".equals(name)) {
            btnNavAttendance.setBackground(UIUtils.PRIMARY);
            attendancePanel.loadDropdownData();
        } else if ("REPORTS".equals(name)) {
            btnNavReports.setBackground(UIUtils.PRIMARY);
        }
    }

    private void resetNavButtonStyles() {
        Color defaultColor = new Color(30, 41, 59);
        btnNavDashboard.setBackground(defaultColor);
        btnNavStudents.setBackground(defaultColor);
        btnNavAttendance.setBackground(defaultColor);
        btnNavReports.setBackground(defaultColor);
    }

    private void logout() {
        if (UIUtils.showConfirmDialog(this, "Are you sure you want to log out?", "Logout")) {
            dispose();
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);
            Admin newAdmin = loginDialog.getLoggedInAdmin();
            if (newAdmin != null) {
                new MainFrame(newAdmin).setVisible(true);
            } else {
                System.exit(0);
            }
        }
    }

    private void startClockTimer() {
        Timer timer = new Timer(1000, e -> {
            String timeStr = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").format(new Date());
            lblClock.setText("🕒 " + timeStr);
        });
        timer.start();
    }
}
