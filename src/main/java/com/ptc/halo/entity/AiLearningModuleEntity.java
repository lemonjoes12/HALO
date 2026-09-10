package com.ptc.halo.entity;

import com.ptc.halo.enums.AiGenerationStatus;
import com.ptc.halo.enums.LessonStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_learning_modules")
public class AiLearningModuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_id", nullable = false)
    private WeekEntity week;

    private String youtubeLink;

    @Column(columnDefinition = "LONGTEXT")
    private String aiNotes;

    @OneToMany(
            mappedBy = "module",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AiLearningFileEntity> files = new ArrayList<>();

    @Column(columnDefinition = "LONGTEXT")
    private String lessonText;


    @Column(columnDefinition = "LONGTEXT")
    private String generatedObjectives;


    @Column(columnDefinition = "LONGTEXT")
    private String generatedKnowledge;


    @Column(columnDefinition = "LONGTEXT")
    private String generatedExamples;


    @Column(columnDefinition = "LONGTEXT")
    private String generatedSummary;


    @Enumerated(EnumType.STRING)
    private LessonStatus status;

    @Enumerated(EnumType.STRING)
    private AiGenerationStatus aiGenerationStatus;


    public AiLearningModuleEntity() {
    }

    public void addFile(AiLearningFileEntity file) {
        files.add(file);
        file.setModule(this);
    }

    public void removeFile(AiLearningFileEntity file) {
        files.remove(file);
        file.setModule(null);
    }

    public Long getId() {
        return id;
    }


    public WeekEntity getWeek() {
        return week;
    }


    public void setWeek(WeekEntity week) {
        this.week = week;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public AiGenerationStatus getAiGenerationStatus() {
        return aiGenerationStatus;
    }

    public void setAiGenerationStatus(
            AiGenerationStatus aiGenerationStatus) {

        this.aiGenerationStatus = aiGenerationStatus;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public List<AiLearningFileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<AiLearningFileEntity> files) {
        this.files = files;
    }

    public String getAiNotes() {
        return aiNotes;
    }


    public void setAiNotes(String aiNotes) {
        this.aiNotes = aiNotes;
    }

    public void setId(Long id) {
        this.id = id;
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