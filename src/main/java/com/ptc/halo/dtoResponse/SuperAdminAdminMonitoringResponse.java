package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.Status;

import java.time.LocalDateTime;

public class SuperAdminAdminMonitoringResponse {

    private Long adminId;
    private String name;
    private String email;
    private Status status;
    private Long accountActivities;
    private LocalDateTime lastActivity;


    public SuperAdminAdminMonitoringResponse() {
    }


    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
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


    public Long getAccountActivities() {
        return accountActivities;
    }

    public void setAccountActivities(Long accountActivities) {
        this.accountActivities = accountActivities;
    }


    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
}