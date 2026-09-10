package com.ptc.halo.dtoResponse;

import java.util.List;

public class AssessmentResponse {

    private Long id;
    private String title;
    private Integer passingScore;
    private List<AssessmentQuestionResponse> questions;

    public AssessmentResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<AssessmentQuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AssessmentQuestionResponse> questions) {
        this.questions = questions;
    }

}