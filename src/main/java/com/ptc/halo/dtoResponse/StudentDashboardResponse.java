package com.ptc.halo.dtoResponse;

public class StudentDashboardResponse {

    private Long completedModules;
    private Long totalBadges;
    private Long passedAssessments;
    private Integer latestAssessmentScore;

    public StudentDashboardResponse() {
    }

    public Long getCompletedModules() {
        return completedModules;
    }

    public void setCompletedModules(Long completedModules) {
        this.completedModules = completedModules;
    }

    public Long getTotalBadges() {
        return totalBadges;
    }

    public void setTotalBadges(Long totalBadges) {
        this.totalBadges = totalBadges;
    }

    public Long getPassedAssessments() {
        return passedAssessments;
    }

    public void setPassedAssessments(Long passedAssessments) {
        this.passedAssessments = passedAssessments;
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