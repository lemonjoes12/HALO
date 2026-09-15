package com.ptc.halo.dtoResponse;

public class AdminDashboardResponse {

    private Long totalStudents;
    private Long totalProfessors;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long totalSubjects;
    private Long totalModules;

    public AdminDashboardResponse() {
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getTotalProfessors() {
        return totalProfessors;
    }

    public void setTotalProfessors(Long totalProfessors) {
        this.totalProfessors = totalProfessors;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(Long inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }

    public Long getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(Long totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public Long getTotalModules() {
        return totalModules;
    }

    public void setTotalModules(Long totalModules) {
        this.totalModules = totalModules;
    }
}