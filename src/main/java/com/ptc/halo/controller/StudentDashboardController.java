package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.StudentDashboardResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.StudentDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/dashboard")
public class StudentDashboardController {

    private final StudentDashboardService
            studentDashboardService;

    private final UserRepository userRepository;


    public StudentDashboardController(
            StudentDashboardService studentDashboardService,
            UserRepository userRepository) {

        this.studentDashboardService =
                studentDashboardService;

        this.userRepository =
                userRepository;
    }


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<StudentDashboardResponse>
    getDashboard(Authentication authentication) {

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
                studentDashboardService
                        .getDashboard(student)
        );
    }
}