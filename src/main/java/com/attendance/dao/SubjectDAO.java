package com.attendance.dao;

import com.attendance.database.DatabaseConnection;
import com.attendance.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Subject Management.
 */
public class SubjectDAO {

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT subject_id, subject_code, subject_name, class_name FROM subjects ORDER BY class_name, subject_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Subject(
                        rs.getInt("subject_id"),
                        rs.getString("subject_code"),
                        rs.getString("subject_name"),
                        rs.getString("class_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("getAllSubjects error: " + e.getMessage());
        }
        return list;
    }

    public List<Subject> getSubjectsByClass(String className) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT subject_id, subject_code, subject_name, class_name FROM subjects WHERE class_name = ? ORDER BY subject_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, className);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Subject(
                            rs.getInt("subject_id"),
                            rs.getString("subject_code"),
                            rs.getString("subject_name"),
                            rs.getString("class_name")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("getSubjectsByClass error: " + e.getMessage());
        }
        return list;
    }

    public boolean addSubject(Subject subject) {
        String sql = "INSERT INTO subjects (subject_code, subject_name, class_name) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, subject.getSubjectCode());
            ps.setString(2, subject.getSubjectName());
            ps.setString(3, subject.getClassName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addSubject error: " + e.getMessage());
            return false;
        }
    }
}
