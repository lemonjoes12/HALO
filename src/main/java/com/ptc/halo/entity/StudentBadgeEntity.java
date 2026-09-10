package com.ptc.halo.entity;

import com.ptc.halo.enums.BadgeType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_badges",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"student_id", "badge_type"}
                )
        }
)
public class StudentBadgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private UserEntity student;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "badge_type",
            nullable = false
    )
    private BadgeType badgeType;

    @Column(nullable = false)
    private String badgeName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime earnedAt;

    public StudentBadgeEntity() {
    }

    public Long getId() {
        return id;
    }

    public UserEntity getStudent() {
        return student;
    }

    public void setStudent(UserEntity student) {
        this.student = student;
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