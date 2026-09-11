package com.ptc.halo.dtoResponse;

public class StudentWeekAccessResponse {

    private Long weekId;
    private Integer weekNumber;
    private String title;

    private Long moduleId;

    private Boolean unlocked;
    private Boolean completed;
    private Boolean lessonAvailable;


    public StudentWeekAccessResponse() {
    }


    public Long getWeekId() {
        return weekId;
    }

    public void setWeekId(Long weekId) {
        this.weekId = weekId;
    }


    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }


    public Boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
    }


    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }


    public Boolean getLessonAvailable() {
        return lessonAvailable;
    }

    public void setLessonAvailable(Boolean lessonAvailable) {
        this.lessonAvailable = lessonAvailable;
    }
}