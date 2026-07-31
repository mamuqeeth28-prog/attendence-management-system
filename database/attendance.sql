-- Attendance Management System SQL Script
-- Database Creation
CREATE DATABASE IF NOT EXISTS attendance_db;
USE attendance_db;

-- 1. Admin Table
CREATE TABLE IF NOT EXISTS admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Subjects Table
CREATE TABLE IF NOT EXISTS subjects (
    subject_id INT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(20) NOT NULL UNIQUE,
    subject_name VARCHAR(100) NOT NULL,
    class_name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Students Table
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

-- 4. Attendance Table
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

-- ================================================
-- INITIAL SEED DATA
-- ================================================

-- Default Admin Account (Username: admin | Password: admin123)
-- SHA-256 hash of 'admin123' is '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9'
INSERT INTO admin (username, password_hash, full_name, email)
SELECT 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'System Administrator', 'admin@attendance.com'
WHERE NOT EXISTS (SELECT 1 FROM admin WHERE username = 'admin');

-- Sample Subjects
INSERT INTO subjects (subject_code, subject_name, class_name) VALUES
('CS101', 'Computer Science Fundamentals', 'CS-A'),
('CS102', 'Data Structures & Algorithms', 'CS-A'),
('MATH201', 'Discrete Mathematics', 'CS-A'),
('ENG101', 'Technical English', 'CS-B')
ON DUPLICATE KEY UPDATE subject_name=VALUES(subject_name);

-- Sample Students
INSERT INTO students (student_id, name, roll_number, email, phone, gender, class_name, section, status) VALUES
('STU-2026-001', 'Alice Smith', 'CS202601', 'alice.smith@example.com', '1234567890', 'Female', 'CS-A', 'A', 'Active'),
('STU-2026-002', 'Bob Johnson', 'CS202602', 'bob.johnson@example.com', '2345678901', 'Male', 'CS-A', 'A', 'Active'),
('STU-2026-003', 'Charlie Brown', 'CS202603', 'charlie.b@example.com', '3456789012', 'Male', 'CS-A', 'A', 'Active'),
('STU-2026-004', 'Diana Prince', 'CS202604', 'diana.p@example.com', '4567890123', 'Female', 'CS-A', 'B', 'Active'),
('STU-2026-005', 'Ethan Hunt', 'CS202605', 'ethan.h@example.com', '5678901234', 'Male', 'CS-B', 'A', 'Active')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Sample Attendance Data for Today
INSERT INTO attendance (student_id, subject_id, date, status, remarks) VALUES
('STU-2026-001', 1, CURDATE(), 'Present', 'On time'),
('STU-2026-002', 1, CURDATE(), 'Present', 'On time'),
('STU-2026-003', 1, CURDATE(), 'Absent', 'Medical leave'),
('STU-2026-004', 1, CURDATE(), 'Present', 'On time')
ON DUPLICATE KEY UPDATE status=VALUES(status);
