package com.ptc.halo.entity;

import com.ptc.halo.enums.AssessmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assessments")
public class AssessmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "module_id",
            nullable = false,
            unique = true
    )
    private AiLearningModuleEntity module;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer passingScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssessmentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "assessment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("questionNumber ASC")
    private List<AssessmentQuestionEntity> questions = new ArrayList<>();

    public AssessmentEntity() {
    }

    public Long getId() {
        return id;
    }

    public AiLearningModuleEntity getModule() {
        return module;
    }

    public void setModule(AiLearningModuleEntity module) {
        this.module = module;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public List<AssessmentQuestionEntity> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AssessmentQuestionEntity> questions) {
        this.questions = questions;
    }

    public void addQuestion(AssessmentQuestionEntity question) {
        questions.add(question);
        question.setAssessment(this);
    }

    public void removeQuestion(AssessmentQuestionEntity question) {
        questions.remove(question);
        question.setAssessment(null);
    }
}