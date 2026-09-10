package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.StudentBadgeResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.BadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/badges")
public class StudentBadgeController {

    private final BadgeService badgeService;
    private final UserRepository userRepository;

    public StudentBadgeController(
            BadgeService badgeService,
            UserRepository userRepository) {

        this.badgeService = badgeService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<StudentBadgeResponse>>
    getStudentBadges(
            Authentication authentication) {

        String email =
                authentication.getName();

        UserEntity student =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        return ResponseEntity.ok(
                badgeService.getStudentBadges(
                        student
                )
        );
    }
}