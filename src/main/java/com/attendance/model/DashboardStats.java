package com.attendance.model;

/**
 * Data Transfer Object for Dashboard metrics.
 */
public class DashboardStats {
    private int totalStudents;
    private int presentToday;
    private int absentToday;
    private int lateToday;
    private double attendancePercentage;

    public DashboardStats() {}

    public DashboardStats(int totalStudents, int presentToday, int absentToday, int lateToday, double attendancePercentage) {
        this.totalStudents = totalStudents;
        this.presentToday = presentToday;
        this.absentToday = absentToday;
        this.lateToday = lateToday;
        this.attendancePercentage = attendancePercentage;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getPresentToday() {
        return presentToday;
    }

    public void setPresentToday(int presentToday) {
        this.presentToday = presentToday;
    }

    public int getAbsentToday() {
        return absentToday;
    }

    public void setAbsentToday(int absentToday) {
        this.absentToday = absentToday;
    }

    public int getLateToday() {
        return lateToday;
    }

    public void setLateToday(int lateToday) {
        this.lateToday = lateToday;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
}
