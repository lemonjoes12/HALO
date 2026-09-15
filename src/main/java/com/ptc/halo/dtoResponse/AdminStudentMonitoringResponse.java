package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.Status;
import com.ptc.halo.enums.YearLevel;

public class AdminStudentMonitoringResponse {

    private Long userId;
    private String studentId;
    private String name;
    private String email;
    private String section;
    private YearLevel yearLevel;
    private Status status;

    private Long completedModules;
    private Long passedAssessments;
    private Long totalBadges;
    private Integer latestAssessmentScore;

    public AdminStudentMonitoringResponse() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public YearLevel getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(YearLevel yearLevel) {
        this.yearLevel = yearLevel;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getCompletedModules() {
        return completedModules;
    }

    public void setCompletedModules(Long completedModules) {
        this.completedModules = completedModules;
    }

    public Long getPassedAssessments() {
        return passedAssessments;
    }

    public void setPassedAssessments(Long passedAssessments) {
        this.passedAssessments = passedAssessments;
    }

    public Long getTotalBadges() {
        return totalBadges;
    }

    public void setTotalBadges(Long totalBadges) {
        this.totalBadges = totalBadges;
    }

    public Integer getLatestAssessmentScore() {
        return latestAssessmentScore;
    }

    public void setLatestAssessmentScore(
            Integer latestAssessmentScore) {
        this.latestAssessmentScore =
                latestAssessmentScore;
    }
}