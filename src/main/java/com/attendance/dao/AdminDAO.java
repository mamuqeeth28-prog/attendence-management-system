package com.attendance.dao;

import com.attendance.database.DatabaseConnection;
import com.attendance.model.Admin;
import com.attendance.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Admin user authentication and profile updates.
 */
public class AdminDAO {

    public Admin authenticate(String username, String plainPassword) {
        String sql = "SELECT id, username, password_hash, full_name, email, created_at FROM admin WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (PasswordUtil.verifyPassword(plainPassword, storedHash)) {
                        return new Admin(
                                rs.getInt("id"),
                                rs.getString("username"),
                                storedHash,
                                rs.getString("full_name"),
                                rs.getString("email"),
                                rs.getTimestamp("created_at")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Admin authenticate error: " + e.getMessage());
        }
        return null;
    }

    public boolean updatePassword(int adminId, String newPlainPassword) {
        String sql = "UPDATE admin SET password_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PasswordUtil.hashPassword(newPlainPassword));
            ps.setInt(2, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Admin updatePassword error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProfile(int adminId, String fullName, String email) {
        String sql = "UPDATE admin SET full_name = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setInt(3, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Admin updateProfile error: " + e.getMessage());
            return false;
        }
    }
}
