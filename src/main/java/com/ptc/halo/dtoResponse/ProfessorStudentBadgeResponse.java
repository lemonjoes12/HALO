package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.BadgeType;

import java.time.LocalDateTime;

public class ProfessorStudentBadgeResponse {

    private Long id;
    private BadgeType badgeType;
    private String badgeName;
    private String description;
    private LocalDateTime earnedAt;

    public ProfessorStudentBadgeResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }
}