package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.MentorMessageRequest;
import com.ptc.halo.dtoResponse.MentorConversationResponse;
import com.ptc.halo.dtoResponse.MentorSessionResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.MentorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/mentor")
public class StudentMentorController {

    private final MentorService mentorService;
    private final UserRepository userRepository;

    public StudentMentorController(
            MentorService mentorService,
            UserRepository userRepository) {

        this.mentorService = mentorService;
        this.userRepository = userRepository;
    }
    @PostMapping("/message/{sessionId}")
    public ResponseEntity<MentorSessionResponse> sendMessage(
            @PathVariable Long sessionId,
            @RequestBody MentorMessageRequest request,
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

        MentorSessionResponse response =
                mentorService.sendMessage(
                        sessionId,
                        student,
                        request.getMessage()
                );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<MentorConversationResponse> getConversation(
            @PathVariable Long sessionId,
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

        MentorConversationResponse response =
                mentorService.getConversation(
                        sessionId,
                        student
                );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/open/{moduleId}")
    public ResponseEntity<MentorConversationResponse> openSession(
            @PathVariable Long moduleId,
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

        MentorConversationResponse response =
                mentorService.openSession(
                        moduleId,
                        student
                );

        return ResponseEntity.ok(response);
    }
}