package com.attendance.ui;

import com.attendance.dao.AttendanceDAO;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.DashboardStats;
import com.attendance.model.ReportFilter;
import com.attendance.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Dashboard Overview Panel displaying Stat Cards, Quick Actions & Today's Summary.
 */
public class DashboardPanel extends JPanel {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final MainFrame mainFrame;

    private JLabel lblTotalStudentsVal;
    private JLabel lblPresentTodayVal;
    private JLabel lblAbsentTodayVal;
    private JLabel lblPercentageVal;

    private JTable tableTodaySummary;
    private DefaultTableModel tableModel;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initUI();
        refreshStats();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(UIUtils.FONT_TITLE);
        title.setForeground(UIUtils.TEXT_DARK);

        JButton btnRefresh = UIUtils.createButton("🔄 Refresh Stats", UIUtils.SECONDARY, Color.WHITE);
        btnRefresh.addActionListener(e -> refreshStats());

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        // Stats Cards Panel (Grid 1x4)
        JPanel statsContainer = new JPanel(new GridLayout(1, 4, 15, 0));
        statsContainer.setOpaque(false);

        JPanel cardTotal = UIUtils.createStatCard("Total Students", "0", UIUtils.PRIMARY, "👨‍🎓");
        JPanel cardPresent = UIUtils.createStatCard("Present Today", "0", UIUtils.SUCCESS, "✅");
        JPanel cardAbsent = UIUtils.createStatCard("Absent Today", "0", UIUtils.DANGER, "❌");
        JPanel cardPct = UIUtils.createStatCard("Attendance Rate", "0.0%", UIUtils.INFO, "📊");

        lblTotalStudentsVal = findValueLabel(cardTotal);
        lblPresentTodayVal = findValueLabel(cardPresent);
        lblAbsentTodayVal = findValueLabel(cardAbsent);
        lblPercentageVal = findValueLabel(cardPct);

        statsContainer.add(cardTotal);
        statsContainer.add(cardPresent);
        statsContainer.add(cardAbsent);
        statsContainer.add(cardPct);

        // Quick Actions & Recent Activity Split
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);

        // Quick Action Side Panel
        JPanel quickActionCard = UIUtils.createCardPanel();
        quickActionCard.setLayout(new BoxLayout(quickActionCard, BoxLayout.Y_AXIS));
        quickActionCard.setPreferredSize(new Dimension(240, 0));

        JLabel qaTitle = new JLabel("Quick Actions");
        qaTitle.setFont(UIUtils.FONT_SUBTITLE);
        qaTitle.setForeground(UIUtils.TEXT_DARK);
        qaTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnMarkAtt = UIUtils.createButton("📝 Mark Attendance", UIUtils.PRIMARY, Color.WHITE);
        btnMarkAtt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnMarkAtt.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnMarkAtt.addActionListener(e -> mainFrame.showPanel("ATTENDANCE"));

        JButton btnAddStud = UIUtils.createButton("➕ Add Student", UIUtils.SUCCESS, Color.WHITE);
        btnAddStud.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnAddStud.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAddStud.addActionListener(e -> mainFrame.showPanel("STUDENTS"));

        JButton btnViewRep = UIUtils.createButton("📈 Generate Reports", UIUtils.INFO, Color.WHITE);
        btnViewRep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnViewRep.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnViewRep.addActionListener(e -> mainFrame.showPanel("REPORTS"));

        quickActionCard.add(qaTitle);
        quickActionCard.add(Box.createVerticalStrut(15));
        quickActionCard.add(btnMarkAtt);
        quickActionCard.add(Box.createVerticalStrut(10));
        quickActionCard.add(btnAddStud);
        quickActionCard.add(Box.createVerticalStrut(10));
        quickActionCard.add(btnViewRep);

        // Recent Activity Table Panel
        JPanel activityCard = UIUtils.createCardPanel();
        activityCard.setLayout(new BorderLayout(10, 10));

        JLabel actTitle = new JLabel("Today's Attendance Summary");
        actTitle.setFont(UIUtils.FONT_SUBTITLE);
        actTitle.setForeground(UIUtils.TEXT_DARK);

        String[] cols = {"Student ID", "Student Name", "Class", "Subject", "Status", "Remarks"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableTodaySummary = new JTable(tableModel);
        UIUtils.styleTable(tableTodaySummary);
        JScrollPane scrollPane = new JScrollPane(tableTodaySummary);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        activityCard.add(actTitle, BorderLayout.NORTH);
        activityCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(quickActionCard, BorderLayout.WEST);
        contentPanel.add(activityCard, BorderLayout.CENTER);

        // Center Wrapper
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 20));
        centerWrapper.setOpaque(false);
        centerWrapper.add(statsContainer, BorderLayout.NORTH);
        centerWrapper.add(contentPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private JLabel findValueLabel(JPanel card) {
        for (Component comp : card.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component subComp : ((JPanel) comp).getComponents()) {
                    if ("cardValueLabel".equals(subComp.getName()) && subComp instanceof JLabel) {
                        return (JLabel) subComp;
                    }
                }
            }
        }
        return new JLabel("0");
    }

    public void refreshStats() {
        DashboardStats stats = attendanceDAO.getDashboardStats();
        lblTotalStudentsVal.setText(String.valueOf(stats.getTotalStudents()));
        lblPresentTodayVal.setText(String.valueOf(stats.getPresentToday()));
        lblAbsentTodayVal.setText(String.valueOf(stats.getAbsentToday()));
        lblPercentageVal.setText(String.format("%.1f%%", stats.getAttendancePercentage()));

        // Load today's attendance summary table
        tableModel.setRowCount(0);
        ReportFilter filter = new ReportFilter();
        filter.setStartDate(new Date(System.currentTimeMillis()));
        filter.setEndDate(new Date(System.currentTimeMillis()));

        List<AttendanceRecord> records = attendanceDAO.getReportRecords(filter);
        for (AttendanceRecord r : records) {
            tableModel.addRow(new Object[]{
                    r.getStudentId(),
                    r.getStudentName(),
                    r.getClassName(),
                    r.getSubjectName(),
                    r.getStatus(),
                    r.getRemarks()
            });
        }
    }
}
