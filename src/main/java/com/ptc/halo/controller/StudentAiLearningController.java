package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.AiLearningModuleResponse;
import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.repository.AiLearningModuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student/ai-learning-modules")
public class StudentAiLearningController {

    private final AiLearningModuleRepository aiLearningModuleRepository;

    public StudentAiLearningController(
            AiLearningModuleRepository aiLearningModuleRepository) {

        this.aiLearningModuleRepository =
                aiLearningModuleRepository;
    }

    @GetMapping("/week/{weekId}")
    public ResponseEntity<AiLearningModuleResponse> getApprovedLesson(
            @PathVariable Long weekId) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository
                        .findByWeekIdAndStatus(
                                weekId,
                                LessonStatus.APPROVED
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Approved lesson not found"
                                )
                        );

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