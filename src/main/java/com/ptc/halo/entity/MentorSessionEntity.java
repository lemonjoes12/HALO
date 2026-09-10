package com.ptc.halo.entity;

import com.ptc.halo.enums.MentorProgressStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor_sessions")
public class MentorSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_learning_module_id", nullable = false)
    private AiLearningModuleEntity module;

    @OneToMany(
            mappedBy = "session",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MentorMessageEntity> messages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorProgressStatus progressStatus =
            MentorProgressStatus.NOT_STARTED;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime lastActivityAt;

    public MentorSessionEntity() {
    }

    public Long getId() {
        return id;
    }
    public List<MentorMessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(List<MentorMessageEntity> messages) {
        this.messages = messages;
    }

    public UserEntity getStudent() {
        return student;
    }

    public MentorProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(
            MentorProgressStatus progressStatus) {

        this.progressStatus = progressStatus;
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

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }
}