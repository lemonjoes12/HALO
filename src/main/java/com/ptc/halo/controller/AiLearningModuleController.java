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
import com.ptc.halo.service.ActivityLogService;
import com.ptc.halo.service.AiGenerationService;
import com.ptc.halo.service.AssessmentService;
import com.ptc.halo.service.FileUploadService;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/professor/ai-learning-modules")
@PreAuthorize("hasRole('PROFESSOR')")
public class AiLearningModuleController {

    private final AiLearningModuleRepository aiLearningModuleRepository;
    private final WeekRepository weekRepository;
    private final FileUploadService fileUploadService;
    private final AiGenerationService aiGenerationService;
    private final AiLearningFileRepository aiLearningFileRepository;
    private final AssessmentService assessmentService;
    private final ActivityLogService activityLogService;
    private final UserRepository userRepository;

    public AiLearningModuleController(
            AiLearningModuleRepository aiLearningModuleRepository,
            WeekRepository weekRepository,
            FileUploadService fileUploadService,
            AiGenerationService aiGenerationService,
            AiLearningFileRepository aiLearningFileRepository,
            AssessmentService assessmentService,
            ActivityLogService activityLogService,
            UserRepository userRepository
    ) {
        this.aiLearningModuleRepository = aiLearningModuleRepository;
        this.weekRepository = weekRepository;
        this.fileUploadService = fileUploadService;
        this.aiGenerationService = aiGenerationService;
        this.aiLearningFileRepository = aiLearningFileRepository;
        this.assessmentService = assessmentService;
        this.activityLogService = activityLogService;
        this.userRepository = userRepository;
    }

    // Returns the existing module, including drafts.
    // 204 means this week does not have a module yet.
    @GetMapping("/week/{weekId}")
    @Transactional(readOnly = true)
    public ResponseEntity<AiLearningModuleResponse> getModuleByWeek(
            @PathVariable("weekId") Long weekId
    ) {
        if (!weekRepository.existsById(weekId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Week not found"
            );
        }

        return aiLearningModuleRepository.findByWeekId(weekId)
                .map(module ->
                        ResponseEntity.ok(convertToResponse(module))
                )
                .orElseGet(() ->
                        ResponseEntity.noContent().build()
                );
    }

    @PostMapping(consumes = "multipart/form-data")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<AiLearningModuleResponse> createModule(
            @RequestParam("weekId") Long weekId,
            @RequestParam(value = "lessonText", required = false)
            String lessonText,
            @RequestParam(value = "youtubeLink", required = false)
            String youtubeLink,
            @RequestParam(value = "aiNotes", required = false)
            String aiNotes,
            @RequestParam(value = "files", required = false)
            List<MultipartFile> files,
            Authentication authentication
    ) throws Exception {

        UserEntity professor = getCurrentUser(authentication);

        WeekEntity week = weekRepository.findById(weekId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Week not found"
                        )
                );

        // Check before writing any uploaded files.
        if (aiLearningModuleRepository.findByWeekId(weekId).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This week already has a module. Open the existing module."
            );
        }

        boolean hasFiles = files != null
                && files.stream().anyMatch(file ->
                file != null && !file.isEmpty()
        );

        if (!hasText(lessonText) && !hasText(youtubeLink) && !hasFiles) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Add lesson text, a YouTube link, or a file."
            );
        }

        AiLearningModuleEntity module = new AiLearningModuleEntity();

        module.setWeek(week);
        module.setLessonText(lessonText);
        module.setYoutubeLink(youtubeLink);
        module.setAiNotes(aiNotes);
        module.setStatus(LessonStatus.PENDING);
        module.setAiGenerationStatus(AiGenerationStatus.PENDING);

        if (files != null) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }

                String filePath = fileUploadService.uploadFile(file);

                AiLearningFileEntity learningFile =
                        new AiLearningFileEntity();

                learningFile.setOriginalFileName(file.getOriginalFilename());
                learningFile.setStoredFileName(
                        Paths.get(filePath).getFileName().toString()
                );
                learningFile.setFileType(file.getContentType());
                learningFile.setFilePath(filePath);

                module.addFile(learningFile);
            }
        }

        // Flush here so database conflicts are raised before returning.
        AiLearningModuleEntity savedModule =
                aiLearningModuleRepository.saveAndFlush(module);

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Created learning module ID " + savedModule.getId()
        );

        return ResponseEntity.ok(convertToResponse(savedModule));
    }

    @PostMapping("/{id}/generate")
    public ResponseEntity<AiLearningModuleResponse> generateLesson(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        UserEntity professor = getCurrentUser(authentication);
        AiLearningModuleEntity module = findModule(id);

        requireEditable(module);

        AiLearningModuleEntity generatedModule =
                aiGenerationService.generateLesson(id);

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Generated AI lesson for module ID " + id
        );

        return ResponseEntity.ok(
                convertToResponse(generatedModule)
        );
    }

    @PutMapping("/{id}/approve")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<AiLearningModuleResponse> approveLesson(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        UserEntity professor = getCurrentUser(authentication);
        AiLearningModuleEntity module = findModule(id);

        if (module.getStatus() == LessonStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This lesson is already approved."
            );
        }

        if (module.getAiGenerationStatus() != AiGenerationStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Generate the lesson successfully before approving it."
            );
        }

        module.setStatus(LessonStatus.APPROVED);

        AiLearningModuleEntity savedModule =
                aiLearningModuleRepository.save(module);

        // If this throws, participating database changes roll back.
        assessmentService.generateAssessment(savedModule.getId());

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Approved AI lesson for module ID " + id
        );

        return ResponseEntity.ok(convertToResponse(savedModule));
    }

    @PutMapping("/{id}/decline")
    @Transactional
    public ResponseEntity<AiLearningModuleResponse> declineLesson(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        UserEntity professor = getCurrentUser(authentication);
        AiLearningModuleEntity module = findModule(id);

        requireEditable(module);

        if (module.getAiGenerationStatus() != AiGenerationStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only completed AI-generated lessons can be declined."
            );
        }

        module.setStatus(LessonStatus.DECLINED);

        AiLearningModuleEntity savedModule =
                aiLearningModuleRepository.save(module);

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Declined AI lesson for module ID " + id
        );

        return ResponseEntity.ok(convertToResponse(savedModule));
    }

    @PostMapping(
            path = "/{moduleId}/files",
            consumes = "multipart/form-data"
    )
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<AiLearningModuleResponse> uploadFile(
            @PathVariable("moduleId") Long moduleId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws Exception {
        UserEntity professor = getCurrentUser(authentication);
        AiLearningModuleEntity module = findModule(moduleId);

        requireEditable(module);

        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Choose a non-empty file."
            );
        }

        AiLearningFileEntity fileEntity =
                fileUploadService.uploadFile(file, module);

        module.addFile(fileEntity);
        aiLearningFileRepository.save(fileEntity);

        // Materials changed: regenerate before approving.
        invalidateGeneratedLesson(module);
        aiLearningModuleRepository.save(module);

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Uploaded file to learning module ID " + moduleId
        );

        return ResponseEntity.ok(convertToResponse(module));
    }

    @DeleteMapping("/files/{fileId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> deleteFile(
            @PathVariable("fileId") Long fileId,
            Authentication authentication
    ) throws Exception {
        UserEntity professor = getCurrentUser(authentication);

        AiLearningFileEntity file =
                aiLearningFileRepository.findById(fileId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "File not found"
                                )
                        );

        AiLearningModuleEntity module = file.getModule();
        requireEditable(module);

        fileUploadService.deleteFile(file.getFilePath());

        module.removeFile(file);
        invalidateGeneratedLesson(module);
        aiLearningModuleRepository.saveAndFlush(module);

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Removed file from learning module ID " + module.getId()
        );

        return ResponseEntity.ok("File deleted successfully");
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<AiLearningModuleResponse> updateModule(
            @PathVariable("id") Long id,
            @RequestBody AiLearningModuleUpdateRequest request,
            Authentication authentication
    ) {
        UserEntity professor = getCurrentUser(authentication);
        AiLearningModuleEntity module = findModule(id);

        requireEditable(module);

        module.setLessonText(request.getLessonText());
        module.setYoutubeLink(request.getYoutubeLink());
        module.setAiNotes(request.getAiNotes());

        invalidateGeneratedLesson(module);

        AiLearningModuleEntity updatedModule =
                aiLearningModuleRepository.save(module);

        activityLogService.createLog(
                professor,
                ActivityType.MODULE,
                "Updated learning module ID " + id
        );

        return ResponseEntity.ok(convertToResponse(updatedModule));
    }

    private AiLearningModuleEntity findModule(Long id) {
        return aiLearningModuleRepository.findWithFilesById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "AI Learning Module not found"
                        )
                );
    }

    private void requireEditable(AiLearningModuleEntity module) {
        if (module.getStatus() == LessonStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Published lessons cannot be changed through the draft editor."
            );
        }
    }

    private void invalidateGeneratedLesson(AiLearningModuleEntity module) {
        module.setStatus(LessonStatus.PENDING);
        module.setAiGenerationStatus(AiGenerationStatus.PENDING);
        module.setGeneratedObjectives(null);
        module.setGeneratedKnowledge(null);
        module.setGeneratedExamples(null);
        module.setGeneratedSummary(null);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private UserEntity getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please sign in."
            );
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        )
                );
    }

    private AiLearningModuleResponse convertToResponse(
            AiLearningModuleEntity module
    ) {
        AiLearningModuleResponse response =
                new AiLearningModuleResponse();

        response.setId(module.getId());
        response.setWeekId(module.getWeek().getId());
        response.setYoutubeLink(module.getYoutubeLink());
        response.setAiNotes(module.getAiNotes());
        response.setLessonText(module.getLessonText());
        response.setGeneratedObjectives(module.getGeneratedObjectives());
        response.setGeneratedKnowledge(module.getGeneratedKnowledge());
        response.setGeneratedExamples(module.getGeneratedExamples());
        response.setGeneratedSummary(module.getGeneratedSummary());
        response.setStatus(module.getStatus());
        response.setAiGenerationStatus(module.getAiGenerationStatus());

        response.setFiles(
                module.getFiles().stream()
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
                            fileResponse.setFileType(file.getFileType());
                            fileResponse.setFilePath(file.getFilePath());

                            return fileResponse;
                        })
                        .collect(Collectors.toList())
        );

        return response;
    }

    // Return explicit JSON errors instead of forwarding these failures
    // to the generic error page.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleRequestError(
            ResponseStatusException exception
    ) {
        String message = exception.getReason() != null
                ? exception.getReason()
                : "The request could not be completed.";

        return ResponseEntity.status(exception.getStatusCode())
                .body(Map.of("message", message));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseConflict(
            DataIntegrityViolationException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "message",
                        "The change conflicts with an existing record. "
                                + "Reload the lesson before trying again."
                ));
    }
}