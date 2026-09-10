package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.Role;
import com.ptc.halo.enums.Status;

public class AdminListResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private Status status;

    public AdminListResponse() {
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