package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.SubjectRequest;
import com.ptc.halo.dtoRequest.SubjectUpdateRequest;
import com.ptc.halo.dtoRequest.WeekRequest;
import com.ptc.halo.dtoRequest.WeekUpdateRequest;
import com.ptc.halo.dtoResponse.SubjectResponse;
import com.ptc.halo.dtoResponse.WeekResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.ProfessorAcademicService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professor")
@PreAuthorize("hasRole('PROFESSOR')")
public class ProfessorAcademicController {

    private final ProfessorAcademicService professorAcademicService;
    private final UserRepository userRepository;

    public ProfessorAcademicController(
            ProfessorAcademicService professorAcademicService,
            UserRepository userRepository) {

        this.professorAcademicService = professorAcademicService;
        this.userRepository = userRepository;
    }


    // =========================
    // SUBJECTS
    // =========================

    @PostMapping("/subjects")
    public ResponseEntity<SubjectResponse> createSubject(
            @RequestBody SubjectRequest request,
            Authentication authentication) {

        UserEntity professor =
                getCurrentProfessor(authentication);

        return ResponseEntity.ok(
                professorAcademicService.createSubject(
                        request,
                        professor
                )
        );
    }


    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectResponse>>
    viewAllSubjects() {

        return ResponseEntity.ok(
                professorAcademicService.viewAllSubjects()
        );
    }


    @GetMapping("/subjects/{id}")
    public ResponseEntity<SubjectResponse> viewSubjectById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                professorAcademicService.viewSubjectById(id)
        );
    }


    @PutMapping("/subjects/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable Long id,
            @RequestBody SubjectUpdateRequest request,
            Authentication authentication) {

        UserEntity professor =
                getCurrentProfessor(authentication);

        return ResponseEntity.ok(
                professorAcademicService.updateSubject(
                        id,
                        request,
                        professor
                )
        );
    }


    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<String> deleteSubject(
            @PathVariable Long id,
            Authentication authentication) {

        UserEntity professor =
                getCurrentProfessor(authentication);

        professorAcademicService.deleteSubject(
                id,
                professor
        );

        return ResponseEntity.ok(
                "Subject deleted successfully"
        );
    }


    // =========================
    // WEEKS
    // =========================

    @PostMapping("/subjects/{subjectId}/weeks")
    public ResponseEntity<WeekResponse> createWeek(
            @PathVariable Long subjectId,
            @RequestBody WeekRequest request,
            Authentication authentication) {

        UserEntity professor =
                getCurrentProfessor(authentication);

        return ResponseEntity.ok(
                professorAcademicService.createWeek(
                        subjectId,
                        request,
                        professor
                )
        );
    }


    @GetMapping("/subjects/{subjectId}/weeks")
    public ResponseEntity<List<WeekResponse>> viewAllWeeks(
            @PathVariable Long subjectId) {

        return ResponseEntity.ok(
                professorAcademicService.viewAllWeeks(
                        subjectId
                )
        );
    }


    @GetMapping("/weeks/{id}")
    public ResponseEntity<WeekResponse> viewWeekById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                professorAcademicService.viewWeekById(id)
        );
    }


    @PutMapping("/weeks/{id}")
    public ResponseEntity<WeekResponse> updateWeek(
            @PathVariable Long id,
            @RequestBody WeekUpdateRequest request,
            Authentication authentication) {

        UserEntity professor =
                getCurrentProfessor(authentication);

        return ResponseEntity.ok(
                professorAcademicService.updateWeek(
                        id,
                        request,
                        professor
                )
        );
    }


    @DeleteMapping("/weeks/{id}")
    public ResponseEntity<String> deleteWeek(
            @PathVariable Long id,
            Authentication authentication) {

        UserEntity professor =
                getCurrentProfessor(authentication);

        professorAcademicService.deleteWeek(
                id,
                professor
        );

        return ResponseEntity.ok(
                "Week deleted successfully"
        );
    }


    // =========================
    // CURRENT PROFESSOR
    // =========================

    private UserEntity getCurrentProfessor(
            Authentication authentication) {

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Professor not found"
                        )
                );
    }
}