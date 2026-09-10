package com.ptc.halo.dtoResponse;

public class AssessmentAnswerFeedbackResponse {

    private Long questionId;
    private Integer questionNumber;
    private String questionText;

    private String studentAnswer;
    private String correctAnswer;

    private Boolean correct;

    public AssessmentAnswerFeedbackResponse() {}

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }
}