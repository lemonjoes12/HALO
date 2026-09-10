package com.ptc.halo.dtoResponse;

public class MentorSessionResponse {

    private Long sessionId;
    private Long moduleId;
    private String haloMessage;

    public MentorSessionResponse() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getHaloMessage() {
        return haloMessage;
    }

    public void setHaloMessage(String haloMessage) {
        this.haloMessage = haloMessage;
    }
}