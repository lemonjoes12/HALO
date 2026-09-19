package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.AiLearningModuleUpdateRequest;
import com.ptc.halo.dtoResponse.AiLearningFileResponse;
import com.ptc.halo.dtoResponse.AiLearningModuleResponse;
import com.ptc.halo.entity.AiLearningFileEntity;
import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.entity.WeekEntity;
import com.ptc.halo.enums.ActivityType;
import com.ptc.halo.enums.AiGenerationStatus;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.repository.AiLearningFileRepository;
import com.ptc.halo.repository.AiLearningModuleRepository;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.repository.WeekRepository;
import com.ptc.halo.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/professor/ai-learning-modules")
public class AiLearningModuleController {

    private final AiLearningModuleRepository aiLearningModuleRepository;
    private final WeekRepository weekRepository;
    private final FileUploadService fileUploadService;
    private final AiGenerationService aiGenerationService;
    private final AiLearningFileRepository aiLearningFileRepository;
    private final AssessmentService assessmentService;
    private final ActivityLogService activityLogService;
    private final UserRepository userRepository;
    private final AiLearningModuleService aiLearningModuleService;

    public AiLearningModuleController(
            AiLearningModuleRepository aiLearningModuleRepository,
            WeekRepository weekRepository,
            FileUploadService fileUploadService,
            AiGenerationService aiGenerationService, AiLearningFileRepository aiLearningFileRepository, AssessmentService assessmentService, ActivityLogService activityLogService, UserRepository userRepository, AiLearningModuleService aiLearningModuleService) {

        this.aiLearningModuleRepository = aiLearningModuleRepository;
        this.weekRepository = weekRepository;
        this.fileUploadService = fileUploadService;
        this.aiGenerationService = aiGenerationService;
        this.aiLearningFileRepository = aiLearningFileRepository;
        this.assessmentService = assessmentService;
        this.activityLogService = activityLogService;
        this.userRepository = userRepository;
        this.aiLearningModuleService = aiLearningModuleService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AiLearningModuleResponse> createModule(

            @RequestParam Long weekId,

            @RequestParam(required = false)
            String lessonText,

            @RequestParam(required = false)
            String youtubeLink,

            @RequestParam(required = false)
            String aiNotes,

            @RequestParam(required = false)
            List<MultipartFile> files,

            Authentication authentication

    ) throws Exception {

        WeekEntity week = weekRepository.findById(weekId)
                .orElseThrow(() ->
                        new RuntimeException("Week not found")
                );

        AiLearningModuleEntity module =
                new AiLearningModuleEntity();

        module.setWeek(week);
        module.setLessonText(lessonText);
        module.setYoutubeLink(youtubeLink);
        module.setAiNotes(aiNotes);

        if (files != null && !files.isEmpty()) {

            for (MultipartFile file : files) {

                if (file == null || file.isEmpty()) {
                    continue;
                }

                String filePath =
                        fileUploadService.uploadFile(file);

                String storedFileName =
                        filePath.substring(
                                filePath.lastIndexOf("\\") + 1
                        );

                AiLearningFileEntity learningFile =
                        new AiLearningFileEntity();

                learningFile.setOriginalFileName(
                        file.getOriginalFilename()
                );

                learningFile.setStoredFileName(
                        storedFileName
                );

                learningFile.setFileType(
                        file.getContentType()
                );

                learningFile.setFilePath(
                        filePath
                );

                module.addFile(learningFile);
            }
        }

        AiLearningModuleEntity savedModule =
                aiLearningModuleRepository.save(module);

        UserEntity professor =
                getCurrentUser(authentication);

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Created learning module ID " + savedModule.getId()
        );

        return ResponseEntity.ok(
                convertToResponse(savedModule)
        );
    }

    @PostMapping("/{id}/generate")
    public ResponseEntity<AiLearningModuleResponse> generateLesson(
            @PathVariable Long id,
            Authentication authentication) {

        AiLearningModuleEntity generatedModule =
                aiGenerationService.generateLesson(id);

        UserEntity professor =
                getCurrentUser(authentication);

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Generated AI lesson for module ID " + id
        );

        return ResponseEntity.ok(
                convertToResponse(generatedModule)
        );
    }

    private AiLearningModuleResponse convertToResponse(
            AiLearningModuleEntity module) {

        AiLearningModuleResponse response =
                new AiLearningModuleResponse();

        response.setId(module.getId());
        response.setWeekId(module.getWeek().getId());

        response.setFiles(
                module.getFiles()
                        .stream()
                        .map(file -> {

                            AiLearningFileResponse fileResponse =
                                    new AiLearningFileResponse();

                            fileResponse.setId(file.getId());
                            fileResponse.setOriginalFileName(
                                    file.getOriginalFileName()
                            );
                            fileResponse.setStoredFileName(
                                    file.getStoredFileName()
                            );
                            fileResponse.setFileType(
                                    file.getFileType()
                            );
                            fileResponse.setFilePath(
                                    file.getFilePath()
                            );

                            return fileResponse;

                        })
                        .collect(Collectors.toList())
        );

        response.setYoutubeLink(module.getYoutubeLink());
        response.setAiNotes(module.getAiNotes());
        response.setLessonText(module.getLessonText());

        response.setGeneratedObjectives(
                module.getGeneratedObjectives()
        );

        response.setGeneratedKnowledge(
                module.getGeneratedKnowledge()
        );

        response.setGeneratedExamples(
                module.getGeneratedExamples()
        );

        response.setGeneratedSummary(
                module.getGeneratedSummary()
        );

        response.setStatus(module.getStatus()
        );

        response.setAiGenerationStatus(
                module.getAiGenerationStatus()
        );

        return response;
    }
    @PutMapping("/{id}/approve")
    public ResponseEntity<AiLearningModuleResponse> approveLesson(
            @PathVariable Long id, Authentication authentication) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI Learning Module not found"
                                )
                        );

        if (module.getAiGenerationStatus() == null ||
                !module.getAiGenerationStatus()
                        .name()
                        .equals("COMPLETED")) {

            throw new RuntimeException(
                    "Only completed AI-generated lessons can be approved"
            );
        }

        module.setStatus(LessonStatus.APPROVED);

        AiLearningModuleEntity savedModule =
                aiLearningModuleRepository.save(module);


        assessmentService.generateAssessment(savedModule.getId());
        UserEntity professor =
                getCurrentUser(authentication);

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Approved AI lesson for module ID " + id
        );

        return ResponseEntity.ok(
                convertToResponse(savedModule)
        );
    }
    @PutMapping("/{id}/decline")
    public ResponseEntity<AiLearningModuleResponse> declineLesson(
            @PathVariable Long id, Authentication authentication) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI Learning Module not found"
                                )
                        );

        if (module.getAiGenerationStatus()
                != AiGenerationStatus.COMPLETED) {

            throw new RuntimeException(
                    "Only completed AI-generated lessons can be declined"
            );
        }

        module.setStatus(LessonStatus.DECLINED);

        AiLearningModuleEntity savedModule =
                aiLearningModuleRepository.save(module);
        UserEntity professor =
                getCurrentUser(authentication);

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Declined AI lesson for module ID " + id
        );

        return ResponseEntity.ok(
                convertToResponse(savedModule)
        );
    }
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<String> deleteFile(
            @PathVariable Long fileId) throws Exception {

        AiLearningFileEntity file =
                aiLearningFileRepository.findById(fileId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "File not found"
                                )
                        );

        String filePath = file.getFilePath();

        fileUploadService.deleteFile(filePath);

        aiLearningFileRepository.delete(file);

        return ResponseEntity.ok(
                "File deleted successfully"
        );
    }
    @PostMapping(
            path = "/{moduleId}/files",
            consumes = "multipart/form-data")
    public ResponseEntity<AiLearningModuleResponse> uploadFile(
            @PathVariable Long moduleId,
            @RequestParam MultipartFile file
    ) throws Exception {

        AiLearningModuleEntity module =
                aiLearningModuleRepository.findById(moduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI Learning Module not found"
                                )
                        );

        AiLearningFileEntity fileEntity =
                fileUploadService.uploadFile(
                        file,
                        module
                );

        aiLearningFileRepository.save(fileEntity);

        return ResponseEntity.ok(
                createResponse(module)
        );
    }
    private AiLearningModuleResponse createResponse(
            AiLearningModuleEntity module
    ) {

        AiLearningModuleResponse response =
                new AiLearningModuleResponse();

        response.setId(module.getId());
        response.setWeekId(module.getWeek().getId());
        response.setYoutubeLink(module.getYoutubeLink());
        response.setAiNotes(module.getAiNotes());
        response.setLessonText(module.getLessonText());

        response.setGeneratedObjectives(
                module.getGeneratedObjectives()
        );

        response.setGeneratedKnowledge(
                module.getGeneratedKnowledge()
        );

        response.setGeneratedExamples(
                module.getGeneratedExamples()
        );

        response.setGeneratedSummary(
                module.getGeneratedSummary()
        );

        response.setStatus(module.getStatus());

        response.setAiGenerationStatus(
                module.getAiGenerationStatus()
        );

        return response;
    }
    @PutMapping("/{id}")
    public ResponseEntity<AiLearningModuleResponse> updateModule(
            @PathVariable Long id,
            @RequestBody AiLearningModuleUpdateRequest request,
            Authentication authentication
    ) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI Learning Module not found"
                                )
                        );

        module.setLessonText(request.getLessonText());
        module.setYoutubeLink(request.getYoutubeLink());
        module.setAiNotes(request.getAiNotes());

        AiLearningModuleEntity updatedModule =
                aiLearningModuleRepository.save(module);

        activityLogService.createLog(
                getCurrentUser(authentication),
                ActivityType.MODULE,
                "Updated learning module ID " + id
        );
        return ResponseEntity.ok(
                createResponse(updatedModule)
        );
    }
    private UserEntity getCurrentUser(
            Authentication authentication) {

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Professor not found"
                        )
                );
    }
    @GetMapping("/week/{weekId}")
    public ResponseEntity<AiLearningModuleResponse> getModuleByWeek(
            @PathVariable Long weekId) {

        AiLearningModuleEntity module =
                aiLearningModuleService.getModuleByWeekId(weekId);

        return ResponseEntity.ok(
                convertToResponse(module)
        );
    }

}