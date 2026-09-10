package com.ptc.halo.dtoResponse;

import lombok.Data;

public class AiGenerationResponse {

    private boolean valid;

    private String reason;

    private String objectives;

    private String knowledge;

    private String examples;

    private String summary;

    public AiGenerationResponse() {
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getExamples() {
        return examples;
    }

    public void setExamples(String examples) {
        this.examples = examples;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}