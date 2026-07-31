package com.attendance.ui;

import com.attendance.dao.StudentDAO;
import com.attendance.model.Student;
import com.attendance.util.UIUtils;
import com.attendance.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Student Management Panel: CRUD operations, Auto-ID, real-time search and table filters.
 */
public class StudentManagementPanel extends JPanel {

    private final StudentDAO studentDAO = new StudentDAO();
    private JTable studentTable;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cbClassFilter;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;

    public StudentManagementPanel() {
        initUI();
        loadStudentData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // Top Header & Search Bar
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);

        JLabel lblTitle = new JLabel("Student Management");
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.TEXT_DARK);

        // Control Bar (Search, Class Filter, Action Buttons)
        JPanel controlBar = UIUtils.createCardPanel();
        controlBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 5));

        JLabel lblSearchIcon = new JLabel("🔍 Search:");
        lblSearchIcon.setFont(UIUtils.FONT_BOLD);

        txtSearch = new JTextField(15);
        txtSearch.setFont(UIUtils.FONT_REGULAR);
        txtSearch.setPreferredSize(new Dimension(180, 32));

        JLabel lblClass = new JLabel("Class:");
        lblClass.setFont(UIUtils.FONT_BOLD);

        cbClassFilter = new JComboBox<>();
        cbClassFilter.setFont(UIUtils.FONT_REGULAR);
        cbClassFilter.setPreferredSize(new Dimension(130, 32));
        cbClassFilter.addItem("All Classes");

        btnAdd = UIUtils.createButton("➕ Add Student", UIUtils.PRIMARY, Color.WHITE);
        btnEdit = UIUtils.createButton("✏️ Edit", UIUtils.INFO, Color.WHITE);
        btnDelete = UIUtils.createButton("🗑️ Delete", UIUtils.DANGER, Color.WHITE);
        btnRefresh = UIUtils.createButton("🔄 Reset", UIUtils.SECONDARY, Color.WHITE);

        controlBar.add(lblSearchIcon);
        controlBar.add(txtSearch);
        controlBar.add(lblClass);
        controlBar.add(cbClassFilter);
        controlBar.add(Box.createHorizontalStrut(10));
        controlBar.add(btnAdd);
        controlBar.add(btnEdit);
        controlBar.add(btnDelete);
        controlBar.add(btnRefresh);

        topContainer.add(lblTitle, BorderLayout.NORTH);
        topContainer.add(controlBar, BorderLayout.SOUTH);

        // Table Panel
        JPanel tableCard = UIUtils.createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] columns = {"Student ID", "Name", "Roll No", "Email", "Phone", "Gender", "Class", "Section", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        UIUtils.styleTable(studentTable);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);

        // Event Handlers
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterStudents(); }
            public void removeUpdate(DocumentEvent e) { filterStudents(); }
            public void changedUpdate(DocumentEvent e) { filterStudents(); }
        });

        cbClassFilter.addActionListener(e -> filterStudents());

        btnAdd.addActionListener(e -> openStudentFormDialog(null));
        btnEdit.addActionListener(e -> editSelectedStudent());
        btnDelete.addActionListener(e -> deleteSelectedStudent());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbClassFilter.setSelectedIndex(0);
            loadStudentData();
        });
    }

    public void loadStudentData() {
        // Populate class filter combo
        String selectedClass = (String) cbClassFilter.getSelectedItem();
        cbClassFilter.removeAllItems();
        cbClassFilter.addItem("All Classes");
        List<String> classes = studentDAO.getDistinctClasses();
        for (String c : classes) {
            cbClassFilter.addItem(c);
        }
        if (selectedClass != null) cbClassFilter.setSelectedItem(selectedClass);

        filterStudents();
    }

    private void filterStudents() {
        String keyword = txtSearch.getText().trim();
        String selectedClass = (String) cbClassFilter.getSelectedItem();

        tableModel.setRowCount(0);
        List<Student> list = studentDAO.searchStudents(keyword, selectedClass);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getName(),
                    s.getRollNumber(),
                    s.getEmail(),
                    s.getPhone(),
                    s.getGender(),
                    s.getClassName(),
                    s.getSection(),
                    s.getStatus()
            });
        }
    }

    private void editSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showErrorDialog(this, "Please select a student from the table to edit.", "Selection Required");
            return;
        }
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        Student student = studentDAO.getStudentById(studentId);
        if (student != null) {
            openStudentFormDialog(student);
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showErrorDialog(this, "Please select a student from the table to delete.", "Selection Required");
            return;
        }
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        if (UIUtils.showConfirmDialog(this, "Are you sure you want to delete student: " + name + " (" + studentId + ")?", "Confirm Delete")) {
            if (studentDAO.deleteStudent(studentId)) {
                UIUtils.showInfoDialog(this, "Student deleted successfully.", "Success");
                loadStudentData();
            } else {
                UIUtils.showErrorDialog(this, "Failed to delete student.", "Error");
            }
        }
    }

    private void openStudentFormDialog(Student existingStudent) {
        boolean isEdit = (existingStudent != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Edit Student Details" : "Add New Student", true);
        dialog.setSize(440, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.CARD_BG);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;

        JTextField txtId = new JTextField(isEdit ? existingStudent.getStudentId() : studentDAO.generateNextStudentId());
        txtId.setEditable(false);
        txtId.setFont(UIUtils.FONT_BOLD);

        JTextField txtName = new JTextField(isEdit ? existingStudent.getName() : "");
        JTextField txtRoll = new JTextField(isEdit ? existingStudent.getRollNumber() : "");
        JTextField txtEmail = new JTextField(isEdit ? existingStudent.getEmail() : "");
        JTextField txtPhone = new JTextField(isEdit ? existingStudent.getPhone() : "");

        JComboBox<String> cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        if (isEdit) cbGender.setSelectedItem(existingStudent.getGender());

        JTextField txtClass = new JTextField(isEdit ? existingStudent.getClassName() : "CS-A");
        JTextField txtSection = new JTextField(isEdit ? existingStudent.getSection() : "A");

        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        if (isEdit) cbStatus.setSelectedItem(existingStudent.getStatus());

        int r = 0;
        panel.add(new JLabel("Student ID:"), getGbc(0, r)); panel.add(txtId, getGbc(1, r++));
        panel.add(new JLabel("Full Name*:"), getGbc(0, r)); panel.add(txtName, getGbc(1, r++));
        panel.add(new JLabel("Roll Number*:"), getGbc(0, r)); panel.add(txtRoll, getGbc(1, r++));
        panel.add(new JLabel("Email:"), getGbc(0, r)); panel.add(txtEmail, getGbc(1, r++));
        panel.add(new JLabel("Phone:"), getGbc(0, r)); panel.add(txtPhone, getGbc(1, r++));
        panel.add(new JLabel("Gender:"), getGbc(0, r)); panel.add(cbGender, getGbc(1, r++));
        panel.add(new JLabel("Class*:"), getGbc(0, r)); panel.add(txtClass, getGbc(1, r++));
        panel.add(new JLabel("Section*:"), getGbc(0, r)); panel.add(txtSection, getGbc(1, r++));
        panel.add(new JLabel("Status:"), getGbc(0, r)); panel.add(cbStatus, getGbc(1, r++));

        JButton btnSave = UIUtils.createButton(isEdit ? "Update Student" : "Save Student", UIUtils.PRIMARY, Color.WHITE);
        JButton btnClose = UIUtils.createButton("Cancel", UIUtils.SECONDARY, Color.WHITE);

        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPnl.setOpaque(false);
        btnPnl.add(btnSave);
        btnPnl.add(btnClose);

        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        panel.add(btnPnl, gbc);

        btnClose.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String roll = txtRoll.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String className = txtClass.getText().trim();
            String section = txtSection.getText().trim();

            if (ValidationUtil.isEmpty(name) || ValidationUtil.isEmpty(roll) ||
                    ValidationUtil.isEmpty(className) || ValidationUtil.isEmpty(section)) {
                UIUtils.showErrorDialog(dialog, "Please fill in all mandatory fields (*).", "Validation Error");
                return;
            }

            if (!ValidationUtil.isValidEmail(email)) {
                UIUtils.showErrorDialog(dialog, "Invalid Email format.", "Validation Error");
                return;
            }

            if (!ValidationUtil.isValidPhone(phone)) {
                UIUtils.showErrorDialog(dialog, "Invalid Phone number format.", "Validation Error");
                return;
            }

            if (studentDAO.isRollNumberExists(roll, isEdit ? existingStudent.getStudentId() : null)) {
                UIUtils.showErrorDialog(dialog, "A student with Roll Number '" + roll + "' already exists!", "Duplicate Roll Number");
                return;
            }

            Student s = new Student();
            s.setStudentId(txtId.getText().trim());
            s.setName(name);
            s.setRollNumber(roll);
            s.setEmail(email);
            s.setPhone(phone);
            s.setGender((String) cbGender.getSelectedItem());
            s.setClassName(className);
            s.setSection(section);
            s.setStatus((String) cbStatus.getSelectedItem());

            boolean success = isEdit ? studentDAO.updateStudent(s) : studentDAO.addStudent(s);
            if (success) {
                UIUtils.showInfoDialog(dialog, "Student saved successfully!", "Success");
                dialog.dispose();
                loadStudentData();
            } else {
                UIUtils.showErrorDialog(dialog, "Failed to save student record to database.", "Database Error");
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private GridBagConstraints getGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        if (x == 1) gbc.weightx = 1.0;
        return gbc;
    }
}
