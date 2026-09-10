package com.ptc.halo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student_answers")
public class StudentAnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private AssessmentAttemptEntity attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private AssessmentQuestionEntity question;

    @Column(nullable = false, length = 1)
    private String answer;

    public StudentAnswerEntity() {
    }

    public Long getId() {
        return id;
    }

    public AssessmentAttemptEntity getAttempt() {
        return attempt;
    }

    public void setAttempt(AssessmentAttemptEntity attempt) {
        this.attempt = attempt;
    }

    public AssessmentQuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(AssessmentQuestionEntity question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}