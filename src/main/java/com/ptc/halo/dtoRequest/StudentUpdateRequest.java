package com.ptc.halo.dtoRequest;

import com.ptc.halo.enums.YearLevel;

public class StudentUpdateRequest {

    private String name;

    private String studentId;

    private String section;

    private YearLevel yearLevel;


    public StudentUpdateRequest() {
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getStudentId() {
        return studentId;
    }


    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
}