package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.Status;
import com.ptc.halo.enums.YearLevel;

public class StudentListResponse {

    private Long id;

    private String name;

    private String email;

    private String studentId;

    private String section;

    private YearLevel yearLevel;

    private Status status;


    public StudentListResponse() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
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


    public Status getStatus() {
        return status;
    }


    public void setStatus(Status status) {
        this.status = status;
    }
}