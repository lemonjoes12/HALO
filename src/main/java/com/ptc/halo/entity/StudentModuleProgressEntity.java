package com.ptc.halo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_module_progress")
public class StudentModuleProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private AiLearningModuleEntity module;

    @Column(nullable = false)
    private Boolean completed = false;

    private LocalDateTime completedAt;

    public StudentModuleProgressEntity() {}

    public Long getId() {
        return id;
    }

    public UserEntity getStudent() {
        return student;
    }

    public void setStudent(UserEntity student) {
        this.student = student;
    }

    public AiLearningModuleEntity getModule() {
        return module;
    }

    public void setModule(AiLearningModuleEntity module) {
        this.module = module;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}