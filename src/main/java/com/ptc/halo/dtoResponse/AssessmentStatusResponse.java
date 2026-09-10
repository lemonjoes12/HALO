package com.ptc.halo.dtoResponse;

public class AssessmentStatusResponse {

    private Long moduleId;
    private Boolean assessmentExists;
    private Boolean assessmentAvailable;
    private Boolean hasUnfinishedAttempt;
    private Boolean alreadyPassed;
    private Boolean canTakeAssessment;

    public AssessmentStatusResponse() {}

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Boolean getAssessmentExists() {
        return assessmentExists;
    }

    public void setAssessmentExists(Boolean assessmentExists) {
        this.assessmentExists = assessmentExists;
    }

    public Boolean getAssessmentAvailable() {
        return assessmentAvailable;
    }

    public void setAssessmentAvailable(Boolean assessmentAvailable) {
        this.assessmentAvailable = assessmentAvailable;
    }

    public Boolean getHasUnfinishedAttempt() {
        return hasUnfinishedAttempt;
    }

    public void setHasUnfinishedAttempt(Boolean hasUnfinishedAttempt) {
        this.hasUnfinishedAttempt = hasUnfinishedAttempt;
    }

    public Boolean getAlreadyPassed() {
        return alreadyPassed;
    }

    public void setAlreadyPassed(Boolean alreadyPassed) {
        this.alreadyPassed = alreadyPassed;
    }

    public Boolean getCanTakeAssessment() {
        return canTakeAssessment;
    }

    public void setCanTakeAssessment(Boolean canTakeAssessment) {
        this.canTakeAssessment = canTakeAssessment;
    }
}