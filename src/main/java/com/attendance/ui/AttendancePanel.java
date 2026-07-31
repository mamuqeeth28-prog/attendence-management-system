package com.attendance.ui;

import com.attendance.dao.AttendanceDAO;
import com.attendance.dao.StudentDAO;
import com.attendance.dao.SubjectDAO;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Subject;
import com.attendance.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Attendance Marking & Updating Panel with batch selection, date picker & quick actions.
 */
public class AttendancePanel extends JPanel {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    private JSpinner dateSpinner;
    private JComboBox<String> cbClass;
    private JComboBox<SubjectItem> cbSubject;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;

    private JButton btnLoad;
    private JButton btnMarkAllPresent;
    private JButton btnMarkAllAbsent;
    private JButton btnSaveAttendance;

    private List<AttendanceRecord> currentRecords = new ArrayList<>();

    public AttendancePanel() {
        initUI();
        loadDropdownData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // Top Header
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);

        JLabel lblTitle = new JLabel("Attendance Marking & Tracking");
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.TEXT_DARK);

        // Filter Bar (Date, Class, Subject)
        JPanel filterCard = UIUtils.createCardPanel();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));

        // Date Picker Spinner
        JLabel lblDate = new JLabel("📅 Date:");
        lblDate.setFont(UIUtils.FONT_BOLD);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(UIUtils.FONT_REGULAR);
        dateSpinner.setPreferredSize(new Dimension(130, 34));

        // Class Combobox
        JLabel lblClass = new JLabel("🏫 Class:");
        lblClass.setFont(UIUtils.FONT_BOLD);

        cbClass = new JComboBox<>();
        cbClass.setFont(UIUtils.FONT_REGULAR);
        cbClass.setPreferredSize(new Dimension(130, 34));

        // Subject Combobox
        JLabel lblSubject = new JLabel("📚 Subject:");
        lblSubject.setFont(UIUtils.FONT_BOLD);

        cbSubject = new JComboBox<>();
        cbSubject.setFont(UIUtils.FONT_REGULAR);
        cbSubject.setPreferredSize(new Dimension(240, 34));

        btnLoad = UIUtils.createButton("🔍 Load Roster", UIUtils.PRIMARY, Color.WHITE);

        filterCard.add(lblDate);
        filterCard.add(dateSpinner);
        filterCard.add(lblClass);
        filterCard.add(cbClass);
        filterCard.add(lblSubject);
        filterCard.add(cbSubject);
        filterCard.add(btnLoad);

        topContainer.add(lblTitle, BorderLayout.NORTH);
        topContainer.add(filterCard, BorderLayout.SOUTH);

        // Attendance Table Card
        JPanel tableCard = UIUtils.createCardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        // Quick Batch Actions & Save Bar
        JPanel actionHeader = new JPanel(new BorderLayout());
        actionHeader.setOpaque(false);

        JLabel lblTableInfo = new JLabel("Student Attendance Roster");
        lblTableInfo.setFont(UIUtils.FONT_SUBTITLE);
        lblTableInfo.setForeground(UIUtils.TEXT_DARK);

        JPanel quickBtnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        quickBtnGroup.setOpaque(false);

        btnMarkAllPresent = UIUtils.createButton("✅ Mark All Present", UIUtils.SUCCESS, Color.WHITE);
        btnMarkAllAbsent = UIUtils.createButton("❌ Mark All Absent", UIUtils.DANGER, Color.WHITE);
        btnSaveAttendance = UIUtils.createButton("💾 Save Attendance", UIUtils.PRIMARY, Color.WHITE);

        quickBtnGroup.add(btnMarkAllPresent);
        quickBtnGroup.add(btnMarkAllAbsent);
        quickBtnGroup.add(btnSaveAttendance);

        actionHeader.add(lblTableInfo, BorderLayout.WEST);
        actionHeader.add(quickBtnGroup, BorderLayout.EAST);

        // Table Setup
        String[] columns = {"Student ID", "Roll No", "Student Name", "Class", "Status", "Remarks"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; // Only Status & Remarks editable
            }
        };

        attendanceTable = new JTable(tableModel);
        UIUtils.styleTable(attendanceTable);

        // Add ComboBox Editor for Status column
        TableColumn statusColumn = attendanceTable.getColumnModel().getColumn(4);
        JComboBox<String> comboStatusEditor = new JComboBox<>(new String[]{"Present", "Absent", "Late"});
        comboStatusEditor.setFont(UIUtils.FONT_REGULAR);
        statusColumn.setCellEditor(new DefaultCellEditor(comboStatusEditor));

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        tableCard.add(actionHeader, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);

        // Event Handlers
        cbClass.addActionListener(e -> updateSubjectDropdown());
        btnLoad.addActionListener(e -> loadRosterData());
        btnMarkAllPresent.addActionListener(e -> setAllStatus("Present"));
        btnMarkAllAbsent.addActionListener(e -> setAllStatus("Absent"));
        btnSaveAttendance.addActionListener(e -> saveAttendanceRecords());
    }

    public void loadDropdownData() {
        cbClass.removeAllItems();
        List<String> classes = studentDAO.getDistinctClasses();
        for (String c : classes) {
            cbClass.addItem(c);
        }
        updateSubjectDropdown();
    }

    private void updateSubjectDropdown() {
        cbSubject.removeAllItems();
        String selectedClass = (String) cbClass.getSelectedItem();
        if (selectedClass != null) {
            List<Subject> subjects = subjectDAO.getSubjectsByClass(selectedClass);
            if (subjects.isEmpty()) {
                // Fallback to all subjects if none match specifically
                subjects = subjectDAO.getAllSubjects();
            }
            for (Subject s : subjects) {
                cbSubject.addItem(new SubjectItem(s.getSubjectId(), s.getSubjectCode() + " - " + s.getSubjectName()));
            }
        }
    }

    private void loadRosterData() {
        String className = (String) cbClass.getSelectedItem();
        SubjectItem selectedSubject = (SubjectItem) cbSubject.getSelectedItem();

        if (className == null || selectedSubject == null) {
            UIUtils.showErrorDialog(this, "Please select Class and Subject.", "Input Required");
            return;
        }

        java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
        Date sqlDate = new Date(utilDate.getTime());

        currentRecords = attendanceDAO.getAttendanceForMarking(className, selectedSubject.id, sqlDate);
        tableModel.setRowCount(0);

        for (AttendanceRecord r : currentRecords) {
            tableModel.addRow(new Object[]{
                    r.getStudentId(),
                    r.getRollNumber(),
                    r.getStudentName(),
                    r.getClassName(),
                    r.getStatus(),
                    r.getRemarks()
            });
        }

        if (currentRecords.isEmpty()) {
            UIUtils.showInfoDialog(this, "No active students found for class: " + className, "Information");
        }
    }

    private void setAllStatus(String status) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(status, i, 4);
        }
    }

    private void saveAttendanceRecords() {
        if (attendanceTable.isEditing()) {
            attendanceTable.getCellEditor().stopCellEditing();
        }

        int rowCount = tableModel.getRowCount();
        if (rowCount == 0) {
            UIUtils.showErrorDialog(this, "No records loaded in table to save.", "Error");
            return;
        }

        SubjectItem selectedSubject = (SubjectItem) cbSubject.getSelectedItem();
        java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
        Date sqlDate = new Date(utilDate.getTime());

        List<AttendanceRecord> recordsToSave = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            String studentId = (String) tableModel.getValueAt(i, 0);
            String status = (String) tableModel.getValueAt(i, 4);
            String remarks = (String) tableModel.getValueAt(i, 5);

            AttendanceRecord r = new AttendanceRecord();
            r.setStudentId(studentId);
            r.setSubjectId(selectedSubject.id);
            r.setDate(sqlDate);
            r.setStatus(status);
            r.setRemarks(remarks != null ? remarks.trim() : "");

            recordsToSave.add(r);
        }

        boolean ok = attendanceDAO.saveOrUpdateAttendanceBatch(recordsToSave);
        if (ok) {
            UIUtils.showInfoDialog(this, "Attendance saved successfully for date: " + new SimpleDateFormat("yyyy-MM-dd").format(sqlDate), "Success");
            loadRosterData();
        } else {
            UIUtils.showErrorDialog(this, "Failed to save attendance records.", "Database Error");
        }
    }

    // Helper wrapper class for Subject ComboBox
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
