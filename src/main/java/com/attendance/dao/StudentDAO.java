package com.attendance.dao;

import com.attendance.database.DatabaseConnection;
import com.attendance.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Data Access Object for Student CRUD, Auto-ID generation, and Search.
 */
public class StudentDAO {

    /**
     * Generate auto-incremented Student ID (e.g. STU-2026-001).
     */
    public synchronized String generateNextStudentId() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String prefix = "STU-" + currentYear + "-";
        String sql = "SELECT student_id FROM students WHERE student_id LIKE ? ORDER BY student_id DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String lastId = rs.getString("student_id");
                    String numPart = lastId.substring(lastId.lastIndexOf('-') + 1);
                    int nextNum = Integer.parseInt(numPart) + 1;
                    return String.format("%s%03d", prefix, nextNum);
                }
            }
        } catch (Exception e) {
            System.err.println("generateNextStudentId error: " + e.getMessage());
        }
        return prefix + "001";
    }

    public boolean isRollNumberExists(String rollNumber, String excludeStudentId) {
        String sql = "SELECT 1 FROM students WHERE roll_number = ?" +
                (excludeStudentId != null ? " AND student_id != ?" : "");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rollNumber.trim());
            if (excludeStudentId != null) {
                ps.setString(2, excludeStudentId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("isRollNumberExists error: " + e.getMessage());
            return false;
        }
    }

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_id, name, roll_number, email, phone, gender, class_name, section, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getStudentId());
            ps.setString(2, student.getName());
            ps.setString(3, student.getRollNumber());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getPhone());
            ps.setString(6, student.getGender());
            ps.setString(7, student.getClassName());
            ps.setString(8, student.getSection());
            ps.setString(9, student.getStatus() != null ? student.getStatus() : "Active");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addStudent error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, roll_number = ?, email = ?, phone = ?, gender = ?, " +
                "class_name = ?, section = ?, status = ? WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getRollNumber());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getPhone());
            ps.setString(5, student.getGender());
            ps.setString(6, student.getClassName());
            ps.setString(7, student.getSection());
            ps.setString(8, student.getStatus());
            ps.setString(9, student.getStudentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateStudent error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStudent(String studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteStudent error: " + e.getMessage());
            return false;
        }
    }

    public Student getStudentById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("getStudentById error: " + e.getMessage());
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("getAllStudents error: " + e.getMessage());
        }
        return students;
    }

    public List<Student> searchStudents(String keyword, String classNameFilter) {
        List<Student> students = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE (student_id LIKE ? OR name LIKE ? OR roll_number LIKE ? OR email LIKE ?)");
        
        if (classNameFilter != null && !classNameFilter.isEmpty() && !"All Classes".equalsIgnoreCase(classNameFilter)) {
            sql.append(" AND class_name = ?");
        }
        sql.append(" ORDER BY student_id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            String term = "%" + keyword.trim() + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);
            ps.setString(4, term);

            if (classNameFilter != null && !classNameFilter.isEmpty() && !"All Classes".equalsIgnoreCase(classNameFilter)) {
                ps.setString(5, classNameFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("searchStudents error: " + e.getMessage());
        }
        return students;
    }

    public List<String> getDistinctClasses() {
        List<String> classes = new ArrayList<>();
        String sql = "SELECT DISTINCT class_name FROM students ORDER BY class_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                classes.add(rs.getString("class_name"));
            }
        } catch (SQLException e) {
            System.err.println("getDistinctClasses error: " + e.getMessage());
        }
        return classes;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("student_id"),
                rs.getString("name"),
                rs.getString("roll_number"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("gender"),
                rs.getString("class_name"),
                rs.getString("section"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}
