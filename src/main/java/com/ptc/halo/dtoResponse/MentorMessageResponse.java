package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.MessageSender;

import java.time.LocalDateTime;

public class MentorMessageResponse {

    private Long id;
    private MessageSender sender;
    private String message;
    private LocalDateTime createdAt;

    public MentorMessageResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageSender getSender() {
        return sender;
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}