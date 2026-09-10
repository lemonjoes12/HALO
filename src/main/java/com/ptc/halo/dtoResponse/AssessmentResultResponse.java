package com.ptc.halo.dtoResponse;

import java.util.List;

public class AssessmentResultResponse {

    private Long attemptId;
    private Integer score;
    private Boolean passed;

    private List<AssessmentAnswerFeedbackResponse> feedback;

    public AssessmentResultResponse() {}

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public List<AssessmentAnswerFeedbackResponse> getFeedback() {
        return feedback;
    }

    public void setFeedback(
            List<AssessmentAnswerFeedbackResponse> feedback) {
        this.feedback = feedback;
    }
}