package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.Status;

public class ProfessorResponse {

    private Long id;
    private String name;
    private String email;
    private String professorId;
    private Status status;

    public ProfessorResponse() {
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

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}