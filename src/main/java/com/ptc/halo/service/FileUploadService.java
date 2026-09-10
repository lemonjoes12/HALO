package com.ptc.halo.service;

import com.ptc.halo.entity.AiLearningFileEntity;
import com.ptc.halo.entity.AiLearningModuleEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    private final Path uploadDirectory =
            Paths.get("uploads/lessons");

    public FileUploadService() throws IOException {
        Files.createDirectories(uploadDirectory);
    }

    public String uploadFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                (!contentType.equalsIgnoreCase("application/pdf")
                        && !contentType.equalsIgnoreCase("image/png")
                        && !contentType.equalsIgnoreCase("image/jpeg"))) {

            throw new RuntimeException(
                    "Only PDF, PNG, and JPG/JPEG files are allowed"
            );
        }

        String originalFilename =
                file.getOriginalFilename();

        if (originalFilename == null ||
                originalFilename.isBlank()) {

            throw new RuntimeException(
                    "Original filename is missing"
            );
        }

        String filename =
                UUID.randomUUID()
                        + "_"
                        + Paths.get(originalFilename)
                        .getFileName()
                        .toString();

        Path filePath =
                uploadDirectory.resolve(filename);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return filePath.toString();
    }
    public void deleteFile(String filePath) throws IOException {

        Path path = Paths.get(filePath);

        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
    public AiLearningFileEntity uploadFile(
            MultipartFile file,
            AiLearningModuleEntity module
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equalsIgnoreCase("application/pdf")
                        || contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/png"))) {

            throw new RuntimeException(
                    "Only PDF, JPG, JPEG, and PNG files are allowed"
            );
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null ||
                originalFileName.isBlank()) {

            throw new RuntimeException(
                    "Invalid file name"
            );
        }

        String extension = "";

        int lastDot =
                originalFileName.lastIndexOf(".");

        if (lastDot >= 0) {
            extension =
                    originalFileName.substring(lastDot);
        }

        String storedFileName =
                UUID.randomUUID() + "_" + originalFileName;

        Path filePath =
                uploadDirectory.resolve(storedFileName);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        AiLearningFileEntity fileEntity =
                new AiLearningFileEntity();

        fileEntity.setModule(module);
        fileEntity.setOriginalFileName(originalFileName);
        fileEntity.setStoredFileName(storedFileName);
        fileEntity.setFilePath(filePath.toString());
        fileEntity.setFileType(contentType);

        return fileEntity;
    }
}