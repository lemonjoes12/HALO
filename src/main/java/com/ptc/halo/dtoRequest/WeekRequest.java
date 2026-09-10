package com.ptc.halo.dtoRequest;

public class WeekRequest {

    private Integer weekNumber;

    private String title;


    public WeekRequest() {
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
}