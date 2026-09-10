package com.ptc.halo.dtoResponse;

import java.util.List;

public class MentorConversationResponse {

    private Long sessionId;
    private Long moduleId;
    private List<MentorMessageResponse> messages;

    public MentorConversationResponse() {
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

    public List<MentorMessageResponse> getMessages() {
        return messages;
    }

    public void setMessages(
            List<MentorMessageResponse> messages) {

        this.messages = messages;
    }
}