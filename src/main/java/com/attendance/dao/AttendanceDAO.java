package com.attendance.dao;

import com.attendance.database.DatabaseConnection;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.DashboardStats;
import com.attendance.model.ReportFilter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Attendance marking, updating, dashboard stats, and reporting.
 */
public class AttendanceDAO {

    /**
     * Get real-time stats for the Dashboard metrics card panel.
     */
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        boolean isSQLite = "SQLITE".equalsIgnoreCase(DatabaseConnection.getDbType());
        String totalSql = "SELECT COUNT(*) FROM students WHERE status = 'Active'";
        String todaySql = isSQLite ?
                "SELECT status, COUNT(*) AS cnt FROM attendance WHERE date = date('now', 'localtime') GROUP BY status" :
                "SELECT status, COUNT(*) AS cnt FROM attendance WHERE date = CURDATE() GROUP BY status";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total Active Students
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(totalSql)) {
                if (rs.next()) {
                    stats.setTotalStudents(rs.getInt(1));
                }
            }

            // Today's Attendance breakdown
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(todaySql)) {
                int present = 0;
                int absent = 0;
                int late = 0;

                while (rs.next()) {
                    String st = rs.getString("status");
                    int count = rs.getInt("cnt");
                    if ("Present".equalsIgnoreCase(st)) present = count;
                    else if ("Absent".equalsIgnoreCase(st)) absent = count;
                    else if ("Late".equalsIgnoreCase(st)) late = count;
                }

                stats.setPresentToday(present);
                stats.setAbsentToday(absent);
                stats.setLateToday(late);

                int totalMarked = present + absent + late;
                if (totalMarked > 0) {
                    double pct = ((double) (present + late) / totalMarked) * 100.0;
                    stats.setAttendancePercentage(Math.round(pct * 10.0) / 10.0);
                } else {
                    stats.setAttendancePercentage(0.0);
                }
            }
        } catch (SQLException e) {
            System.err.println("getDashboardStats error: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Fetch existing attendance for a class/subject on a given date.
     */
    public List<AttendanceRecord> getAttendanceForMarking(String className, int subjectId, Date date) {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT s.student_id, s.name, s.roll_number, s.class_name, " +
                "a.attendance_id, a.status, a.remarks " +
                "FROM students s " +
                "LEFT JOIN attendance a ON s.student_id = a.student_id AND a.subject_id = ? AND a.date = ? " +
                "WHERE s.class_name = ? AND s.status = 'Active' " +
                "ORDER BY s.roll_number, s.name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            ps.setDate(2, date);
            ps.setString(3, className);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord rec = new AttendanceRecord();
                    rec.setAttendanceId(rs.getInt("attendance_id"));
                    rec.setStudentId(rs.getString("student_id"));
                    rec.setStudentName(rs.getString("name"));
                    rec.setRollNumber(rs.getString("roll_number"));
                    rec.setClassName(rs.getString("class_name"));
                    rec.setSubjectId(subjectId);
                    rec.setDate(date);

                    String status = rs.getString("status");
                    rec.setStatus(status != null ? status : "Present"); // Default to Present
                    rec.setRemarks(rs.getString("remarks") != null ? rs.getString("remarks") : "");

                    records.add(rec);
                }
            }
        } catch (SQLException e) {
            System.err.println("getAttendanceForMarking error: " + e.getMessage());
        }
        return records;
    }

    /**
     * Batch save or update attendance records (supports MySQL & SQLite).
     */
    public boolean saveOrUpdateAttendanceBatch(List<AttendanceRecord> records) {
        boolean isSQLite = "SQLITE".equalsIgnoreCase(DatabaseConnection.getDbType());
        String sql = isSQLite ?
                "INSERT OR REPLACE INTO attendance (student_id, subject_id, date, status, remarks) VALUES (?, ?, ?, ?, ?)" :
                "INSERT INTO attendance (student_id, subject_id, date, status, remarks) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE status = VALUES(status), remarks = VALUES(remarks)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (AttendanceRecord rec : records) {
                    ps.setString(1, rec.getStudentId());
                    ps.setInt(2, rec.getSubjectId());
                    ps.setDate(3, rec.getDate());
                    ps.setString(4, rec.getStatus());
                    ps.setString(5, rec.getRemarks());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("saveOrUpdateAttendanceBatch error: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("saveOrUpdateAttendanceBatch connection error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Query detailed reports based on ReportFilter criteria.
     */
    public List<AttendanceRecord> getReportRecords(ReportFilter filter) {
        List<AttendanceRecord> records = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.attendance_id, a.student_id, s.name AS student_name, s.roll_number, s.class_name, " +
                "sub.subject_id, sub.subject_name, a.date, a.status, a.remarks, a.created_at " +
                "FROM attendance a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "JOIN subjects sub ON a.subject_id = sub.subject_id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (filter.getClassName() != null && !filter.getClassName().isEmpty() && !"All Classes".equalsIgnoreCase(filter.getClassName())) {
            sql.append("AND s.class_name = ? ");
            params.add(filter.getClassName());
        }

        if (filter.getSubjectId() != null && filter.getSubjectId() > 0) {
            sql.append("AND sub.subject_id = ? ");
            params.add(filter.getSubjectId());
        }

        if (filter.getStudentId() != null && !filter.getStudentId().isEmpty()) {
            sql.append("AND a.student_id = ? ");
            params.add(filter.getStudentId());
        }

        if (filter.getStartDate() != null) {
            sql.append("AND a.date >= ? ");
            params.add(filter.getStartDate());
        }

        if (filter.getEndDate() != null) {
            sql.append("AND a.date <= ? ");
            params.add(filter.getEndDate());
        }

        sql.append("ORDER BY a.date DESC, s.roll_number ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Date) {
                    ps.setDate(i + 1, (Date) p);
                } else if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else {
                    ps.setString(i + 1, p.toString());
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(new AttendanceRecord(
                            rs.getInt("attendance_id"),
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("roll_number"),
                            rs.getString("class_name"),
                            rs.getInt("subject_id"),
                            rs.getString("subject_name"),
                            rs.getDate("date"),
                            rs.getString("status"),
                            rs.getString("remarks"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("getReportRecords error: " + e.getMessage());
        }
        return records;
    }
}
