package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.AiLearningModuleResponse;
import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.repository.AiLearningModuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ptc.halo.service.StudentLessonAccessService;
import com.ptc.halo.service.StudentLearningProgressionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RestController
@PreAuthorize("hasRole('STUDENT')")
@RequestMapping("/api/student/ai-learning-modules")
@Transactional(readOnly = true)
public class StudentAiLearningController {
    private final StudentLessonAccessService access;
    private final StudentLearningProgressionService progression;

    private final AiLearningModuleRepository aiLearningModuleRepository;

    public StudentAiLearningController(
            AiLearningModuleRepository aiLearningModuleRepository, StudentLessonAccessService access, StudentLearningProgressionService progression) {
        this.access = access;
        this.progression = progression;

        this.aiLearningModuleRepository =
                aiLearningModuleRepository;
    }

    @GetMapping("/week/{weekId}")
    public ResponseEntity<AiLearningModuleResponse> getApprovedLesson(
            @PathVariable("weekId") Long weekId, Authentication authentication) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository
                        .findByWeekId(weekId)
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "MODULE_NOT_FOUND")
                        );

        progression.validateModuleAccess(module.getId(), access.requireStudent(authentication));

        return ResponseEntity.ok(
                convertToResponse(module)
        );
    }

    private AiLearningModuleResponse convertToResponse(
            AiLearningModuleEntity module) {

        AiLearningModuleResponse response =
                new AiLearningModuleResponse();

        response.setId(module.getId());
        response.setWeekId(module.getWeek().getId());

        response.setYoutubeLink(
                module.getYoutubeLink()
        );

        response.setLessonText(
                module.getLessonText()
        );

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

        response.setStatus(
                module.getStatus()
        );

        response.setAiGenerationStatus(
                module.getAiGenerationStatus()
        );

        return response;
    }
}