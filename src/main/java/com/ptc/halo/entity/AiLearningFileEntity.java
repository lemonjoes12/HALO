package com.ptc.halo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ai_learning_files")
public class AiLearningFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ai_learning_module_id",
            nullable = false
    )
    private AiLearningModuleEntity module;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileType;

    public AiLearningFileEntity() {
    }

    public Long getId() {
        return id;
    }

    public AiLearningModuleEntity getModule() {
        return module;
    }

    public void setModule(AiLearningModuleEntity module) {
        this.module = module;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}