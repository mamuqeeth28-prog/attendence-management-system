package com.attendance.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model representing an Attendance entry for a student on a specific date and subject.
 */
public class AttendanceRecord {
    private int attendanceId;
    private String studentId;
    private String studentName;
    private String rollNumber;
    private String className;
    private int subjectId;
    private String subjectName;
    private Date date;
    private String status; // Present, Absent, Late
    private String remarks;
    private Timestamp createdAt;

    public AttendanceRecord() {}

    public AttendanceRecord(int attendanceId, String studentId, String studentName, String rollNumber,
                            String className, int subjectId, String subjectName, Date date,
                            String status, String remarks, Timestamp createdAt) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.className = className;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.date = date;
        this.status = status;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
