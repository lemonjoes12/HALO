package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.ProfessorStudentProgressResponse;
import com.ptc.halo.dtoResponse.ProfessorStudentResponse;
import com.ptc.halo.dtoResponse.StudentSubjectResponse;
import com.ptc.halo.service.ProfessorStudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professor/students")
public class ProfessorStudentController {

    private final ProfessorStudentService
            professorStudentService;

    public ProfessorStudentController(
            ProfessorStudentService professorStudentService) {

        this.professorStudentService =
                professorStudentService;
    }


    @PreAuthorize("hasRole('PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<ProfessorStudentResponse>>
    getStudents() {

        return ResponseEntity.ok(
                professorStudentService.getStudents()
        );
    }
    @PreAuthorize("hasRole('PROFESSOR')")
    @GetMapping("/{userId}/progress")
    public ResponseEntity<ProfessorStudentProgressResponse>
    getStudentProgress(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                professorStudentService
                        .getStudentProgress(userId)
        );
    }
    @PreAuthorize("hasRole('PROFESSOR')")
    @GetMapping("/{userId}/subjects")
    public ResponseEntity<List<StudentSubjectResponse>>
    getStudentSubjects(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                professorStudentService
                        .getStudentSubjects(userId)
        );
    }
}