# 🎓 Attendance Management System (Java & MySQL)

An enterprise-grade, desktop Attendance Management System built with **Java 17+**, **Java Swing (FlatLaf Modern Theme)**, **JDBC (HikariCP Connection Pool)**, and **MySQL Database**. 

Built with MVC (Model-View-Controller) architecture, clean design patterns, real-time metrics dashboard, student CRUD management, batch attendance tracking, OpenPDF report generation, and database backup/restore tools.

---

## 🌟 Key Features

1. **Modern FlatLaf GUI**: Professional, dark slate side-navigation, vibrant stat cards, custom badges for `Present`/`Absent`/`Late`, and styled `JTable` controls.
2. **Secure Admin Authentication**: SHA-256 hashed password verification, user profile management, change password security tool.
3. **Dashboard Overview**: Dynamic metrics cards showing Total Active Students, Present Today, Absent Today, and Overall Attendance Percentage.
4. **Student Management**:
   - Auto-generated Student ID (e.g. `STU-2026-001`).
   - Add, Edit, Delete (with confirmation), and Search students in real-time.
   - Unique Roll Number validation & input constraint enforcement.
5. **Attendance Marking & Tracking**:
   - Class and Subject selection.
   - Date selection picker.
   - Batch status toggle ("Mark All Present", "Mark All Absent").
   - Duplicate entry prevention (updates existing record seamlessly).
6. **Reports & Analytics**:
   - Filter by Daily, Monthly, or Student-wise criteria.
   - Real-time attendance percentage calculation.
   - One-click export to **PDF** (OpenPDF) and **CSV**.
7. **Database Backup & Restore**: GUI-driven export of database schema and data to `.sql` files, with full restore capabilities.

---

## 📁 Directory & Folder Structure

```
attendence/
├── pom.xml                                  # Maven dependencies & build file
├── README.md                                # Setup & User Guide
├── database/
│   └── attendance.sql                       # Complete Database Schema & Seed Data
└── src/main/java/com/attendance/
    ├── App.java                             # Main Application Entry Point
    ├── database/
    │   ├── DatabaseConnection.java          # HikariCP JDBC Connection Pool
    │   └── DatabaseInitializer.java         # Automatic database schema initializer
    ├── model/
    │   ├── Admin.java                       # Admin User Model
    │   ├── Student.java                     # Student Entity Model
    │   ├── Subject.java                     # Subject Model
    │   ├── AttendanceRecord.java            # Attendance Entry Model
    │   ├── DashboardStats.java              # Dashboard Summary DTO
    │   └── ReportFilter.java                # Filter Criteria Model
    ├── dao/
    │   ├── AdminDAO.java                    # Admin authentication & profile updates
    │   ├── StudentDAO.java                  # Student CRUD, Auto-ID & Search DAO
    │   ├── SubjectDAO.java                  # Subject lookup DAO
    │   └── AttendanceDAO.java               # Attendance batch updates & reporting DAO
    ├── util/
    │   ├── PasswordUtil.java                # SHA-256 Hashing Utility
    │   ├── ValidationUtil.java              # Form input & constraint validation
    │   ├── UIUtils.java                     # Modern Swing styling & component builder
    │   ├── PDFExporter.java                 # OpenPDF Report Generator
    │   ├── CSVExporter.java                 # Table CSV Exporter
    │   └── DBBackupUtil.java                # Database SQL Backup & Restore
    └── ui/
        ├── MainFrame.java                   # Primary Application Window (Sidebar & Cards)
        ├── LoginDialog.java                 # Admin Login & DB Credentials configuration
        ├── DashboardPanel.java              # Overview Cards & Daily Activity Table
        ├── StudentManagementPanel.java      # Student CRUD & Real-time Search Panel
        ├── AttendancePanel.java             # Batch Attendance Marking Panel
        ├── ReportsPanel.java                # Analytical Reports & Export Panel
        ├── ChangePasswordDialog.java        # Admin Account Settings Dialog
        └── DBBackupDialog.java              # SQL Backup / Restore GUI Dialog
```

---

## 🛠️ Prerequisites

- **Java Development Kit (JDK 17 or later)** installed and configured in system `PATH`.
- **MySQL Server (8.0 or later)** running on `localhost:3306`.
- **Apache Maven** (or IDE Maven integration in Antigravity IDE / VS Code / IntelliJ / Eclipse).

---

## 🚀 Setup & Execution Guide

### 1. Set Up MySQL Database

1. Open your MySQL client (Command Line, MySQL Workbench, or phpMyAdmin).
2. Run the script provided in `database/attendance.sql`:
   ```bash
   mysql -u root -p < database/attendance.sql
   ```
   *(Note: The application also includes `DatabaseInitializer` which automatically creates tables on launch if they do not exist!)*

### 2. Default Login Credentials

- **Username**: `admin`
- **Password**: `admin123`

*(Note: You can change the username, full name, and password inside the app under **Account Settings**).*

---

## 💻 Running in Antigravity IDE / Terminal

### Method A: Maven Command Line
Navigate to the project root directory and run:

```bash
# Compile project
mvn clean compile

# Launch Application
mvn exec:java
```

### Method B: Configure Database Credentials in GUI
If your MySQL password is not `root`, simply:
1. Launch the application.
2. On the Login screen, click **DB Settings**.
3. Enter your MySQL host, port, username, and password.
4. Click **OK** to test connection and save!

---

## 📄 License & Credits
Built for **Attendance Management System** project requirements using Java Swing & MySQL.
