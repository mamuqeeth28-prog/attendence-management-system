package com.attendance.model;

/**
 * Model representing a Subject/Course entity.
 */
public class Subject {
    private int subjectId;
    private String subjectCode;
    private String subjectName;
    private String className;

    public Subject() {}

    public Subject(int subjectId, String subjectCode, String subjectName, String className) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.className = className;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return subjectCode + " - " + subjectName + " (" + className + ")";
    }
}
