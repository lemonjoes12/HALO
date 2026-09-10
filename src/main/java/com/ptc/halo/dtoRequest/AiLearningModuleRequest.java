package com.ptc.halo.dtoRequest;

public class AiLearningModuleRequest {


    private String uploadedPdf;


    private String lessonText;


    private String youtubeLink;


    private String aiNotes;


    public AiLearningModuleRequest() {
    }


    public String getUploadedPdf() {
        return uploadedPdf;
    }


    public void setUploadedPdf(String uploadedPdf) {
        this.uploadedPdf = uploadedPdf;
    }


    public String getLessonText() {
        return lessonText;
    }


    public void setLessonText(String lessonText) {
        this.lessonText = lessonText;
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
}