package com.ptc.halo.service;

import com.ptc.halo.entity.*;
import com.ptc.halo.enums.*;
import com.ptc.halo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentLessonAccessService {
    private final UserRepository users;
    private final StudentProfileRepository profiles;

    public StudentLessonAccessService(UserRepository users, StudentProfileRepository profiles) {
        this.users = users;
        this.profiles = profiles;
    }

    public UserEntity requireStudent(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED");
        }
        UserEntity student = users.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED"));
        validateStudent(student);
        return student;
    }

    public void validateStudent(UserEntity student) {
        if (student == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED");
        if (student.getRole() != Role.STUDENT) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "INVALID_ROLE");
        if (student.getStatus() != Status.ACTIVE) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ACCOUNT_INACTIVE");
    }

    public void validateSubject(UserEntity student, SubjectEntity subject) {
        validateStudent(student);
        StudentProfileEntity profile = profiles.findByUserId(student.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "STUDENT_NOT_ENROLLED"));
        // Existing enrollment model: subjects are assigned by student year level.
        if (profile.getYearLevel() == null || profile.getYearLevel() != subject.getYearLevel()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "STUDENT_NOT_ENROLLED");
        }
    }

    public void validatePublished(AiLearningModuleEntity module, UserEntity student) {
        validateSubject(student, module.getWeek().getSubject());
        if (module.getStatus() != LessonStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "LESSON_NOT_APPROVED");
        }
        if (module.getAiGenerationStatus() != AiGenerationStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "LESSON_NOT_GENERATED");
        }
    }
}
