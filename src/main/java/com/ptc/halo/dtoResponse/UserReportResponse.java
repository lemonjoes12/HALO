package com.ptc.halo.dtoResponse;

public class UserReportResponse {

    private long totalUsers;
    private long totalStudents;
    private long totalProfessors;
    private long totalAdmins;

    private long activeUsers;
    private long inactiveUsers;


    public UserReportResponse() {
    }


    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }


    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }


    public long getTotalProfessors() {
        return totalProfessors;
    }

    public void setTotalProfessors(long totalProfessors) {
        this.totalProfessors = totalProfessors;
    }


    public long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }


    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }


    public long getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(long inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }
}