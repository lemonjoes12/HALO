package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.ProfessorProfileUpdateRequest;
import com.ptc.halo.dtoResponse.ProfessorProfileResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professor")
public class ProfessorProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;

    public ProfessorProfileController(
            ProfileService profileService,
            UserRepository userRepository) {

        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfessorProfileResponse> getProfile(
            Authentication authentication) {

        UserEntity professor =
                userRepository
                        .findByEmail(authentication.getName())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Professor not found"
                                )
                        );

        return ResponseEntity.ok(
                profileService.getProfessorProfile(professor)
        );
    }
    @PutMapping("/profile")
    public ResponseEntity<ProfessorProfileResponse> updateProfile(
            @RequestBody ProfessorProfileUpdateRequest request,
            Authentication authentication) {

        UserEntity professor =
                userRepository
                        .findByEmail(authentication.getName())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Professor not found"
                                )
                        );

        return ResponseEntity.ok(
                profileService.updateProfessorProfile(
                        professor,
                        request
                )
        );
    }
}