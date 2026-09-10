package com.ptc.halo.dtoResponse;

public class WeekResponse {

    private Long id;

    private Integer weekNumber;

    private String title;

    private Long subjectId;


    public WeekResponse() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
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


    public Long getSubjectId() {
        return subjectId;
    }


    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}