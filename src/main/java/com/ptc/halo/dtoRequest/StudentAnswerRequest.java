package com.ptc.halo.dtoRequest;

import java.util.List;

public class StudentAnswerRequest {

    private List<AnswerItem> answers;

    public StudentAnswerRequest() {
    }

    public List<AnswerItem> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerItem> answers) {
        this.answers = answers;
    }

    public static class AnswerItem {

        private Long questionId;
        private String answer;

        public AnswerItem() {
        }

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}