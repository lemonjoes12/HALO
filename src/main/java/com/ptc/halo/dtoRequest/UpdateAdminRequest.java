package com.ptc.halo.dtoRequest;

public class UpdateAdminRequest {

    private String name;
    private String email;

    public UpdateAdminRequest() {
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
}