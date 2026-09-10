package com.ptc.halo.dtoRequest;

import com.ptc.halo.enums.Status;

public class StatusRequest {

    private Status status;


    public StatusRequest() {
    }


    public Status getStatus() {
        return status;
    }


    public void setStatus(Status status) {
        this.status = status;
    }
}