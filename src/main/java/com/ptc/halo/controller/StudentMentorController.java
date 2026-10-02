package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.MentorMessageRequest;
import com.ptc.halo.dtoResponse.MentorConversationResponse;
import com.ptc.halo.dtoResponse.MentorSessionResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.service.StudentLessonAccessService;
import org.springframework.security.access.prepost.PreAuthorize;
import com.ptc.halo.service.MentorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('STUDENT')")
@RequestMapping("/api/student/mentor")
public class StudentMentorController {

    private final MentorService mentorService;
    private final StudentLessonAccessService access;

    public StudentMentorController(
            MentorService mentorService,
            StudentLessonAccessService access) {

        this.mentorService = mentorService;
        this.access = access;
    }
    @PostMapping("/message/{sessionId}")
    public ResponseEntity<MentorSessionResponse> sendMessage(
            @PathVariable("sessionId") Long sessionId,
            @RequestBody MentorMessageRequest request,
            Authentication authentication) {

        UserEntity student = access.requireStudent(authentication);

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
            @PathVariable("sessionId") Long sessionId,
            Authentication authentication) {

        UserEntity student = access.requireStudent(authentication);

        MentorConversationResponse response =
                mentorService.getConversation(
                        sessionId,
                        student
                );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/open/{moduleId}")
    public ResponseEntity<MentorConversationResponse> openSession(
            @PathVariable("moduleId") Long moduleId,
            Authentication authentication) {

        UserEntity student = access.requireStudent(authentication);

        MentorConversationResponse response =
                mentorService.openSession(
                        moduleId,
                        student
                );

        return ResponseEntity.ok(response);
    }
}