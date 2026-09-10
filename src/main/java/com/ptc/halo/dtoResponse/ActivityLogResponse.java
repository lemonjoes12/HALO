package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.ActivityType;
import com.ptc.halo.enums.Role;

import java.time.LocalDateTime;

public class ActivityLogResponse {

    private Long id;
    private String userName;
    private String userEmail;
    private Role userRole;
    private ActivityType activityType;
    private String action;
    private LocalDateTime createdAt;

    public ActivityLogResponse() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Role getUserRole() {
        return userRole;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}