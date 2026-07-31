package com.attendance.model;

import java.sql.Timestamp;

/**
 * Model representing a Student entity.
 */
public class Student {
    private String studentId;
    private String name;
    private String rollNumber;
    private String email;
    private String phone;
    private String gender;
    private String className;
    private String section;
    private String status; // Active, Inactive
    private Timestamp createdAt;

    public Student() {}

    public Student(String studentId, String name, String rollNumber, String email, String phone,
                   String gender, String className, String section, String status, Timestamp createdAt) {
        this.studentId = studentId;
        this.name = name;
        this.rollNumber = rollNumber;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.className = className;
        this.section = section;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name + " (" + studentId + ")";
    }
}
