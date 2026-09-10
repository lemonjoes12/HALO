package com.ptc.halo.dtoRequest;

public class ProfessorUpdateRequest {

    private String name;

    private String email;

    private String professorId;


    public ProfessorUpdateRequest() {
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


    public String getProfessorId() {
        return professorId;
    }


    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }
}