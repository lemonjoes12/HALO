package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.StudentModuleProgressResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.StudentProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/progress")
public class StudentProgressController {

    private final StudentProgressService studentProgressService;
    private final UserRepository userRepository;

    public StudentProgressController(
            StudentProgressService studentProgressService,
            UserRepository userRepository) {

        this.studentProgressService =
                studentProgressService;

        this.userRepository =
                userRepository;
    }

    @GetMapping
    public ResponseEntity<List<StudentModuleProgressResponse>>
    getProgress(Authentication authentication) {

        String email = authentication.getName();

        UserEntity student =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        List<StudentModuleProgressResponse> response =
                studentProgressService.getProgress(student);

        return ResponseEntity.ok(response);
    }
}