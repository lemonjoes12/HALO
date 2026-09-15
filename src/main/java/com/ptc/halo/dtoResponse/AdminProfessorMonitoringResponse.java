package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.Status;

import java.time.LocalDateTime;

public class AdminProfessorMonitoringResponse {

    private Long userId;
    private String professorId;
    private String name;
    private String email;
    private Status status;
    private Long moduleActivities;
    private LocalDateTime lastActivity;


    public AdminProfessorMonitoringResponse() {
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
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


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public Long getModuleActivities() {
        return moduleActivities;
    }

    public void setModuleActivities(Long moduleActivities) {
        this.moduleActivities = moduleActivities;
    }


    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
}