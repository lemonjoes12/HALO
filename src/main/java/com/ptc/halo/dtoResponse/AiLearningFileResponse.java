package com.ptc.halo.dtoResponse;

public class AiLearningFileResponse {

    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String fileType;
    private String filePath;

    public AiLearningFileResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}