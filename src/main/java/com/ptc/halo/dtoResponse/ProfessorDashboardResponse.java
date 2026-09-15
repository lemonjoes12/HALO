package com.ptc.halo.dtoResponse;

public class ProfessorDashboardResponse {

    private Long totalStudents;
    private Long totalSubjects;
    private Long totalModules;
    private Long approvedModules;
    private Long totalAssessments;
    private Long totalPassedAttempts;

    public ProfessorDashboardResponse() {
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
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

    public Long getApprovedModules() {
        return approvedModules;
    }

    public void setApprovedModules(Long approvedModules) {
        this.approvedModules = approvedModules;
    }

    public Long getTotalAssessments() {
        return totalAssessments;
    }

    public void setTotalAssessments(Long totalAssessments) {
        this.totalAssessments = totalAssessments;
    }

    public Long getTotalPassedAttempts() {
        return totalPassedAttempts;
    }

    public void setTotalPassedAttempts(Long totalPassedAttempts) {
        this.totalPassedAttempts = totalPassedAttempts;
    }
}