package com.attendance.database;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Initializes Database tables and default records (supports MySQL & SQLite).
 */
public class DatabaseInitializer {

    public static boolean initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            boolean isSQLite = "SQLITE".equalsIgnoreCase(DatabaseConnection.getDbType());

            if (isSQLite) {
                // SQLite DDL
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS admin (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        full_name TEXT NOT NULL,
                        email TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                """);

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS subjects (
                        subject_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        subject_code TEXT NOT NULL UNIQUE,
                        subject_name TEXT NOT NULL,
                        class_name TEXT NOT NULL
                    );
                """);

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS students (
                        student_id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        roll_number TEXT NOT NULL UNIQUE,
                        email TEXT,
                        phone TEXT,
                        gender TEXT NOT NULL,
                        class_name TEXT NOT NULL,
                        section TEXT NOT NULL,
                        status TEXT DEFAULT 'Active',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                """);

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS attendance (
                        attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        student_id TEXT NOT NULL,
                        subject_id INTEGER NOT NULL,
                        date TEXT NOT NULL,
                        status TEXT NOT NULL,
                        remarks TEXT DEFAULT '',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE (student_id, subject_id, date),
                        FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                        FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE
                    );
                """);

                // Seed Default Admin for SQLite
                stmt.executeUpdate("""
                    INSERT OR IGNORE INTO admin (username, password_hash, full_name, email)
                    VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'System Administrator', 'admin@attendance.com');
                """);

                // Seed Sample Subjects
                stmt.executeUpdate("""
                    INSERT OR IGNORE INTO subjects (subject_code, subject_name, class_name) VALUES
                    ('CS101', 'Computer Science Fundamentals', 'CS-A'),
                    ('CS102', 'Data Structures & Algorithms', 'CS-A'),
                    ('MATH201', 'Discrete Mathematics', 'CS-A');
                """);

                // Seed Sample Students
                stmt.executeUpdate("""
                    INSERT OR IGNORE INTO students (student_id, name, roll_number, email, phone, gender, class_name, section, status) VALUES
                    ('STU-2026-001', 'Alice Smith', 'CS202601', 'alice.smith@example.com', '1234567890', 'Female', 'CS-A', 'A', 'Active'),
                    ('STU-2026-002', 'Bob Johnson', 'CS202602', 'bob.johnson@example.com', '2345678901', 'Male', 'CS-A', 'A', 'Active');
                """);

            } else {
                // MySQL DDL
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS admin (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        password_hash VARCHAR(64) NOT NULL,
                        full_name VARCHAR(100) NOT NULL,
                        email VARCHAR(100),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS subjects (
                        subject_id INT AUTO_INCREMENT PRIMARY KEY,
                        subject_code VARCHAR(20) NOT NULL UNIQUE,
                        subject_name VARCHAR(100) NOT NULL,
                        class_name VARCHAR(50) NOT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS students (
                        student_id VARCHAR(20) PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        roll_number VARCHAR(30) NOT NULL UNIQUE,
                        email VARCHAR(100),
                        phone VARCHAR(20),
                        gender VARCHAR(10) NOT NULL,
                        class_name VARCHAR(50) NOT NULL,
                        section VARCHAR(10) NOT NULL,
                        status VARCHAR(20) DEFAULT 'Active',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS attendance (
                        attendance_id INT AUTO_INCREMENT PRIMARY KEY,
                        student_id VARCHAR(20) NOT NULL,
                        subject_id INT NOT NULL,
                        date DATE NOT NULL,
                        status ENUM('Present', 'Absent', 'Late') NOT NULL,
                        remarks VARCHAR(255) DEFAULT '',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE KEY uq_student_subject_date (student_id, subject_id, date),
                        FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE ON UPDATE CASCADE,
                        FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE ON UPDATE CASCADE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

                stmt.executeUpdate("""
                    INSERT INTO admin (username, password_hash, full_name, email)
                    SELECT 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'System Administrator', 'admin@attendance.com'
                    WHERE NOT EXISTS (SELECT 1 FROM admin WHERE username = 'admin');
                """);

                stmt.executeUpdate("""
                    INSERT INTO subjects (subject_code, subject_name, class_name)
                    SELECT 'CS101', 'Computer Science Fundamentals', 'CS-A'
                    WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE subject_code = 'CS101');
                """);

                stmt.executeUpdate("""
                    INSERT INTO subjects (subject_code, subject_name, class_name)
                    SELECT 'CS102', 'Data Structures & Algorithms', 'CS-A'
                    WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE subject_code = 'CS102');
                """);

                stmt.executeUpdate("""
                    INSERT INTO students (student_id, name, roll_number, email, phone, gender, class_name, section, status)
                    SELECT 'STU-2026-001', 'Alice Smith', 'CS202601', 'alice.smith@example.com', '1234567890', 'Female', 'CS-A', 'A', 'Active'
                    WHERE NOT EXISTS (SELECT 1 FROM students WHERE student_id = 'STU-2026-001');
                """);
            }

            System.out.println("Database Initialized successfully in " + DatabaseConnection.getDbType() + " mode.");
            return true;
        } catch (Exception e) {
            System.err.println("Database Initialization Error: " + e.getMessage());
            return false;
        }
    }
}
