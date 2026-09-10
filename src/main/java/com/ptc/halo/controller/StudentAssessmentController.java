package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.StudentAnswerRequest;
import com.ptc.halo.dtoResponse.*;
import com.ptc.halo.entity.AssessmentAttemptEntity;
import com.ptc.halo.entity.AssessmentEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.AssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/assessment")
public class StudentAssessmentController {

    private final AssessmentService assessmentService;
    private final UserRepository userRepository;

    public StudentAssessmentController(
            AssessmentService assessmentService, UserRepository userRepository) {

        this.assessmentService = assessmentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/generate/{moduleId}")
    public ResponseEntity<Map<String, Object>> generateAssessment(
            @PathVariable Long moduleId) {

        AssessmentEntity assessment =
                assessmentService.generateAssessment(moduleId);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Assessment generated successfully");
        response.put("assessmentId", assessment.getId());
        response.put("title", assessment.getTitle());
        response.put(
                "questionCount",
                assessment.getQuestions().size()
        );
        response.put("passingScore", assessment.getPassingScore());
        response.put("status", assessment.getStatus());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{moduleId}")
    public ResponseEntity<AssessmentResponse> getAssessment(
            @PathVariable Long moduleId) {

        AssessmentResponse response =
                assessmentService.getAssessment(moduleId);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/start/{moduleId}")
    public ResponseEntity<AssessmentAttemptResponse> startAttempt(
            @PathVariable Long moduleId,
            Authentication authentication) {

        String email = authentication.getName();

        UserEntity student =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        AssessmentAttemptEntity attempt =
                assessmentService.startAttempt(
                        moduleId,
                        student
                );

        AssessmentAttemptResponse response =
                new AssessmentAttemptResponse();

        response.setAttemptId(attempt.getId());
        response.setAssessmentId(
                attempt.getAssessment().getId()
        );
        response.setStartedAt(
                attempt.getStartedAt()
        );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/submit/{attemptId}")
    public ResponseEntity<AssessmentResultResponse> submitAttempt(
            @PathVariable Long attemptId,
            @RequestBody StudentAnswerRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        UserEntity student =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        AssessmentAttemptEntity attempt =
                assessmentService.submitAttempt(
                        attemptId,
                        student,
                        request
                );

        AssessmentResultResponse response =
                new AssessmentResultResponse();

        response.setAttemptId(attempt.getId());
        response.setScore(attempt.getScore());
        response.setPassed(attempt.getPassed());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/attempts/{moduleId}")
    public ResponseEntity<List<AssessmentAttemptHistoryResponse>> getAttemptHistory(
            @PathVariable Long moduleId,
            Authentication authentication) {

        String email = authentication.getName();

        UserEntity student =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        List<AssessmentAttemptHistoryResponse> response =
                assessmentService.getAttemptHistory(
                        moduleId,
                        student
                );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/result/{attemptId}")
    public ResponseEntity<AssessmentResultResponse> getAttemptResult(
            @PathVariable Long attemptId,
            Authentication authentication) {

        String email = authentication.getName();

        UserEntity student =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        AssessmentResultResponse response =
                assessmentService.getAttemptResult(
                        attemptId,
                        student
                );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/status/{moduleId}")
    public ResponseEntity<AssessmentStatusResponse> getAssessmentStatus(
            @PathVariable Long moduleId,
            Authentication authentication) {

        String email = authentication.getName();

        UserEntity student =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        AssessmentStatusResponse response =
                assessmentService.getAssessmentStatus(
                        moduleId,
                        student
                );

        return ResponseEntity.ok(response);
    }
}