package com.attendance.ui;

import com.attendance.dao.AttendanceDAO;
import com.attendance.dao.StudentDAO;
import com.attendance.dao.SubjectDAO;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.ReportFilter;
import com.attendance.model.Subject;
import com.attendance.util.CSVExporter;
import com.attendance.util.PDFExporter;
import com.attendance.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Attendance Reports & Analytics Panel with PDF and CSV export capabilities.
 */
public class ReportsPanel extends JPanel {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    private JComboBox<String> cbReportType;
    private JComboBox<String> cbClass;
    private JComboBox<SubjectItem> cbSubject;
    private JTextField txtStudentId;
    private JSpinner spinnerStartDate;
    private JSpinner spinnerEndDate;

    private JTable reportTable;
    private DefaultTableModel tableModel;

    private JLabel lblTotalCount;
    private JLabel lblPresentCount;
    private JLabel lblAbsentCount;
    private JLabel lblRate;

    private JButton btnGenerate;
    private JButton btnExportPDF;
    private JButton btnExportCSV;

    public ReportsPanel() {
        initUI();
        loadDropdowns();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header Title
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);

        JLabel lblTitle = new JLabel("Attendance Reports & Analytics");
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.TEXT_DARK);

        // Filter Controls Card
        JPanel filterCard = UIUtils.createCardPanel();
        filterCard.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        // Report Type
        cbReportType = new JComboBox<>(new String[]{"Daily Attendance", "Monthly Attendance", "Student-Wise Report"});
        cbReportType.setFont(UIUtils.FONT_REGULAR);

        cbClass = new JComboBox<>();
        cbClass.setFont(UIUtils.FONT_REGULAR);
        cbClass.addItem("All Classes");

        cbSubject = new JComboBox<>();
        cbSubject.setFont(UIUtils.FONT_REGULAR);

        txtStudentId = new JTextField(10);
        txtStudentId.setFont(UIUtils.FONT_REGULAR);

        // Dates
        Calendar cal = Calendar.getInstance();
        spinnerEndDate = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        spinnerEndDate.setEditor(new JSpinner.DateEditor(spinnerEndDate, "yyyy-MM-dd"));

        cal.add(Calendar.DAY_OF_MONTH, -30);
        spinnerStartDate = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        spinnerStartDate.setEditor(new JSpinner.DateEditor(spinnerStartDate, "yyyy-MM-dd"));

        btnGenerate = UIUtils.createButton("⚡ Generate Report", UIUtils.PRIMARY, Color.WHITE);
        btnExportPDF = UIUtils.createButton("📄 Export PDF", UIUtils.DANGER, Color.WHITE);
        btnExportCSV = UIUtils.createButton("📊 Export CSV", UIUtils.SUCCESS, Color.WHITE);

        int col = 0, row = 0;
        filterCard.add(new JLabel("Report Type:"), getGbc(col++, row));
        filterCard.add(cbReportType, getGbc(col++, row));
        filterCard.add(new JLabel("Class:"), getGbc(col++, row));
        filterCard.add(cbClass, getGbc(col++, row));
        filterCard.add(new JLabel("Subject:"), getGbc(col++, row));
        filterCard.add(cbSubject, getGbc(col++, row));

        col = 0; row = 1;
        filterCard.add(new JLabel("Student ID:"), getGbc(col++, row));
        filterCard.add(txtStudentId, getGbc(col++, row));
        filterCard.add(new JLabel("Start Date:"), getGbc(col++, row));
        filterCard.add(spinnerStartDate, getGbc(col++, row));
        filterCard.add(new JLabel("End Date:"), getGbc(col++, row));
        filterCard.add(spinnerEndDate, getGbc(col++, row));

        // Button Group
        JPanel btnGrp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGrp.setOpaque(false);
        btnGrp.add(btnGenerate);
        btnGrp.add(btnExportPDF);
        btnGrp.add(btnExportCSV);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6;
        filterCard.add(btnGrp, gbc);

        topContainer.add(lblTitle, BorderLayout.NORTH);
        topContainer.add(filterCard, BorderLayout.SOUTH);

        // Stats Summary Strip
        JPanel summaryPanel = UIUtils.createCardPanel();
        summaryPanel.setLayout(new GridLayout(1, 4, 10, 0));

        lblTotalCount = new JLabel("Total Records: 0", SwingConstants.CENTER);
        lblPresentCount = new JLabel("Present: 0", SwingConstants.CENTER);
        lblAbsentCount = new JLabel("Absent: 0", SwingConstants.CENTER);
        lblRate = new JLabel("Attendance Rate: 0.0%", SwingConstants.CENTER);

        lblTotalCount.setFont(UIUtils.FONT_BOLD);
        lblPresentCount.setFont(UIUtils.FONT_BOLD);
        lblPresentCount.setForeground(UIUtils.SUCCESS);
        lblAbsentCount.setFont(UIUtils.FONT_BOLD);
        lblAbsentCount.setForeground(UIUtils.DANGER);
        lblRate.setFont(UIUtils.FONT_BOLD);
        lblRate.setForeground(UIUtils.PRIMARY);

        summaryPanel.add(lblTotalCount);
        summaryPanel.add(lblPresentCount);
        summaryPanel.add(lblAbsentCount);
        summaryPanel.add(lblRate);

        // Table Panel
        JPanel tableCard = UIUtils.createCardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        String[] cols = {"Date", "Student ID", "Roll No", "Name", "Class", "Subject", "Status", "Remarks"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        reportTable = new JTable(tableModel);
        UIUtils.styleTable(reportTable);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        tableCard.add(summaryPanel, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);

        // Event Handlers
        btnGenerate.addActionListener(e -> generateReport());
        btnExportPDF.addActionListener(e -> exportPDF());
        btnExportCSV.addActionListener(e -> exportCSV());
    }

    private void loadDropdowns() {
        cbClass.removeAllItems();
        cbClass.addItem("All Classes");
        for (String c : studentDAO.getDistinctClasses()) {
            cbClass.addItem(c);
        }

        cbSubject.removeAllItems();
        cbSubject.addItem(new SubjectItem(0, "All Subjects"));
        for (Subject s : subjectDAO.getAllSubjects()) {
            cbSubject.addItem(new SubjectItem(s.getSubjectId(), s.getSubjectCode() + " - " + s.getSubjectName()));
        }
    }

    private void generateReport() {
        ReportFilter filter = new ReportFilter();
        filter.setReportType((String) cbReportType.getSelectedItem());
        filter.setClassName((String) cbClass.getSelectedItem());

        SubjectItem selectedSubject = (SubjectItem) cbSubject.getSelectedItem();
        if (selectedSubject != null && selectedSubject.id > 0) {
            filter.setSubjectId(selectedSubject.id);
        }

        String studentId = txtStudentId.getText().trim();
        if (!studentId.isEmpty()) {
            filter.setStudentId(studentId);
        }

        java.util.Date startDateUtil = (java.util.Date) spinnerStartDate.getValue();
        java.util.Date endDateUtil = (java.util.Date) spinnerEndDate.getValue();

        filter.setStartDate(new Date(startDateUtil.getTime()));
        filter.setEndDate(new Date(endDateUtil.getTime()));

        List<AttendanceRecord> list = attendanceDAO.getReportRecords(filter);

        tableModel.setRowCount(0);
        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;

        for (AttendanceRecord r : list) {
            tableModel.addRow(new Object[]{
                    r.getDate().toString(),
                    r.getStudentId(),
                    r.getRollNumber(),
                    r.getStudentName(),
                    r.getClassName(),
                    r.getSubjectName(),
                    r.getStatus(),
                    r.getRemarks()
            });

            if ("Present".equalsIgnoreCase(r.getStatus())) presentCount++;
            else if ("Absent".equalsIgnoreCase(r.getStatus())) absentCount++;
            else if ("Late".equalsIgnoreCase(r.getStatus())) lateCount++;
        }

        int total = list.size();
        double pct = (total > 0) ? (((double) (presentCount + lateCount) / total) * 100.0) : 0.0;

        lblTotalCount.setText("Total Records: " + total);
        lblPresentCount.setText("Present: " + presentCount);
        lblAbsentCount.setText("Absent: " + absentCount);
        lblRate.setText(String.format("Attendance Rate: %.1f%%", pct));
    }

    private void exportPDF() {
        if (tableModel.getRowCount() == 0) {
            UIUtils.showErrorDialog(this, "No report data available to export. Click Generate first.", "Empty Export");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF Report");
        fileChooser.setSelectedFile(new File("Attendance_Report_" + new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dest = fileChooser.getSelectedFile();
            if (!dest.getName().endsWith(".pdf")) {
                dest = new File(dest.getAbsolutePath() + ".pdf");
            }

            String title = (String) cbReportType.getSelectedItem() + " (" + cbClass.getSelectedItem() + ")";
            boolean success = PDFExporter.exportTableToPDF(reportTable, title, dest);
            if (success) {
                UIUtils.showInfoDialog(this, "PDF Report exported successfully:\n" + dest.getAbsolutePath(), "Export Success");
            } else {
                UIUtils.showErrorDialog(this, "Failed to export PDF file.", "Export Error");
            }
        }
    }

    private void exportCSV() {
        if (tableModel.getRowCount() == 0) {
            UIUtils.showErrorDialog(this, "No report data available to export. Click Generate first.", "Empty Export");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV Report");
        fileChooser.setSelectedFile(new File("Attendance_Report_" + new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dest = fileChooser.getSelectedFile();
            if (!dest.getName().endsWith(".csv")) {
                dest = new File(dest.getAbsolutePath() + ".csv");
            }

            boolean success = CSVExporter.exportTableToCSV(reportTable, dest);
            if (success) {
                UIUtils.showInfoDialog(this, "CSV Report exported successfully:\n" + dest.getAbsolutePath(), "Export Success");
            } else {
                UIUtils.showErrorDialog(this, "Failed to export CSV file.", "Export Error");
            }
        }
    }

    private GridBagConstraints getGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private static class SubjectItem {
        int id;
        String name;

        SubjectItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
