package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.YearLevel;

public class StudentSubjectResponse {

    private Long subjectId;

    private String subjectCode;

    private String subjectName;

    private YearLevel yearLevel;

    private Integer totalWeeks;

    private Integer completedWeeks;

    private Integer progressPercentage;

    public StudentSubjectResponse() {
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
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

    public YearLevel getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(YearLevel yearLevel) {
        this.yearLevel = yearLevel;
    }

    public Integer getTotalWeeks() {
        return totalWeeks;
    }

    public void setTotalWeeks(Integer totalWeeks) {
        this.totalWeeks = totalWeeks;
    }

    public Integer getCompletedWeeks() {
        return completedWeeks;
    }

    public void setCompletedWeeks(Integer completedWeeks) {
        this.completedWeeks = completedWeeks;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(
            Integer progressPercentage) {

        this.progressPercentage =
                progressPercentage;
    }
}