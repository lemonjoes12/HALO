package com.ptc.halo.entity;

import com.ptc.halo.enums.ActivityType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ActivityLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column
    private ActivityType activityType;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ActivityLogEntity() {
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

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public UserEntity getUser() {
        return user;
    }


    public void setUser(UserEntity user) {
        this.user = user;
    }
}