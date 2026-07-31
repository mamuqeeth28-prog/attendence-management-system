package com.attendance.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC Connection Manager with automatic fallback to embedded SQLite if MySQL is not installed/running.
 */
public class DatabaseConnection {

    private static String dbType = "MYSQL"; // "MYSQL" or "SQLITE"
    private static String jdbcUrl = "jdbc:mysql://localhost:3306/attendance_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String username = "root";
    private static String password = "root";

    private static HikariDataSource dataSource;

    static {
        initDataSource();
    }

    private static synchronized void initDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        try {
            // Attempt MySQL connection with short timeout
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(3000); // 3 seconds timeout to quickly detect missing MySQL server

            dataSource = new HikariDataSource(config);
            dbType = "MYSQL";
            System.out.println("Connected using MySQL Connection Pool.");
        } catch (Exception e) {
            System.out.println("MySQL server not found or authentication failed. Switching to embedded SQLite mode (attendance.db)...");
            dbType = "SQLITE";
            dataSource = null;
        }
    }

    /**
     * Get a connection (either from HikariCP MySQL pool or direct SQLite file driver).
     */
    public static Connection getConnection() throws SQLException {
        if ("SQLITE".equals(dbType)) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC Driver not found", e);
            }
            return DriverManager.getConnection("jdbc:sqlite:attendance.db");
        }

        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }

        if (dataSource == null) {
            // Fallback to SQLite if MySQL failed
            dbType = "SQLITE";
            return getConnection();
        }

        try {
            return dataSource.getConnection();
        } catch (SQLException sqle) {
            System.err.println("MySQL connection error: " + sqle.getMessage() + ". Switching to embedded SQLite mode.");
            dbType = "SQLITE";
            return getConnection();
        }
    }

    public static String getDbType() {
        return dbType;
    }

    public static void setCredentials(String host, String port, String dbName, String user, String pass) {
        jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, dbName);
        username = user;
        password = pass;
        dbType = "MYSQL";
        initDataSource();
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
