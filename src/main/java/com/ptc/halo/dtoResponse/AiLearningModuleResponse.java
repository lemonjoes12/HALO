package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.AiGenerationStatus;
import com.ptc.halo.enums.LessonStatus;

import java.util.List;

public class AiLearningModuleResponse {

    private Long id;

    private Long weekId;

    private List<AiLearningFileResponse> files;

    private String youtubeLink;

    private String aiNotes;

    private String lessonText;

    private String generatedObjectives;

    private String generatedKnowledge;

    private String generatedExamples;

    private String generatedSummary;

    private LessonStatus status;

    private AiGenerationStatus aiGenerationStatus;


    public AiLearningModuleResponse() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getWeekId() {
        return weekId;
    }

    public void setWeekId(Long weekId) {
        this.weekId = weekId;
    }

    public AiGenerationStatus getAiGenerationStatus() {
        return aiGenerationStatus;
    }

    public void setAiGenerationStatus(AiGenerationStatus aiGenerationStatus) {
        this.aiGenerationStatus = aiGenerationStatus;
    }

    public List<AiLearningFileResponse> getFiles() {
        return files;
    }

    public void setFiles(List<AiLearningFileResponse> files) {
        this.files = files;
    }


    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }


    public String getAiNotes() {
        return aiNotes;
    }

    public void setAiNotes(String aiNotes) {
        this.aiNotes = aiNotes;
    }


    public String getLessonText() {
        return lessonText;
    }

    public void setLessonText(String lessonText) {
        this.lessonText = lessonText;
    }


    public String getGeneratedObjectives() {
        return generatedObjectives;
    }

    public void setGeneratedObjectives(String generatedObjectives) {
        this.generatedObjectives = generatedObjectives;
    }


    public String getGeneratedKnowledge() {
        return generatedKnowledge;
    }

    public void setGeneratedKnowledge(String generatedKnowledge) {
        this.generatedKnowledge = generatedKnowledge;
    }


    public String getGeneratedExamples() {
        return generatedExamples;
    }

    public void setGeneratedExamples(String generatedExamples) {
        this.generatedExamples = generatedExamples;
    }


    public String getGeneratedSummary() {
        return generatedSummary;
    }

    public void setGeneratedSummary(String generatedSummary) {
        this.generatedSummary = generatedSummary;
    }


    public LessonStatus getStatus() {
        return status;
    }

    public void setStatus(LessonStatus status) {
        this.status = status;
    }
}