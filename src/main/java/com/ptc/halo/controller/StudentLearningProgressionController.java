package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.StudentSubjectResponse;
import com.ptc.halo.dtoResponse.StudentWeekAccessResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.StudentLearningProgressionService;
import com.ptc.halo.service.StudentSubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/subjects")
public class StudentLearningProgressionController {

    private final StudentLearningProgressionService
            progressionService;
    private final StudentSubjectService
            studentSubjectService;

    private final UserRepository userRepository;


    public StudentLearningProgressionController(
            StudentLearningProgressionService progressionService, StudentSubjectService studentSubjectService,
            UserRepository userRepository) {

        this.progressionService =
                progressionService;
        this.studentSubjectService = studentSubjectService;

        this.userRepository =
                userRepository;
    }


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{subjectId}/weeks")
    public ResponseEntity<List<StudentWeekAccessResponse>>
    getSubjectWeeks(

            @PathVariable Long subjectId,
            Authentication authentication) {


        UserEntity student =
                userRepository
                        .findByEmail(
                                authentication.getName()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );


        return ResponseEntity.ok(
                progressionService
                        .getWeekAccess(
                                subjectId,
                                student
                        )
        );
    }
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<List<StudentSubjectResponse>>
    getStudentSubjects(
            Authentication authentication) {

        UserEntity student =
                userRepository
                        .findByEmail(
                                authentication.getName()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        return ResponseEntity.ok(
                studentSubjectService
                        .getStudentSubjects(student)
        );
    }

}