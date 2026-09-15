package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.StudentProfileResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;

    public StudentProfileController(
            ProfileService profileService,
            UserRepository userRepository) {

        this.profileService = profileService;
        this.userRepository = userRepository;
    }


    @GetMapping("/profile")
    public ResponseEntity<StudentProfileResponse> getProfile(
            Authentication authentication) {

        UserEntity student =
                userRepository
                        .findByEmail(authentication.getName())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        return ResponseEntity.ok(
                profileService.getStudentProfile(student)
        );
    }
}