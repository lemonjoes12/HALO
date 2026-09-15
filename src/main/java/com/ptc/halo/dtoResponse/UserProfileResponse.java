package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.Role;
import com.ptc.halo.enums.Status;

public class UserProfileResponse {

    private Long userId;
    private String name;
    private String email;
    private Role role;
    private Status status;

    public UserProfileResponse() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}