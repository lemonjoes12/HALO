package com.ptc.halo;

import com.ptc.halo.entity.*;
import com.ptc.halo.enums.*;
import com.ptc.halo.repository.*;
import com.ptc.halo.service.*;
import java.util.Optional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentLessonAccessTest {
    UserRepository users = mock(UserRepository.class);
    StudentProfileRepository profiles = mock(StudentProfileRepository.class);
    StudentLessonAccessService access = new StudentLessonAccessService(users, profiles);
    UserEntity student = new UserEntity();
    SubjectEntity subject = new SubjectEntity();
    AiLearningModuleEntity module = mock(AiLearningModuleEntity.class);
    WeekEntity week = mock(WeekEntity.class);

    @BeforeEach void setup() {
        student.setId(8L); student.setRole(Role.STUDENT); student.setStatus(Status.ACTIVE);
        subject.setYearLevel(YearLevel.FIRST_YEAR);
        StudentProfileEntity profile = new StudentProfileEntity();
        profile.setYearLevel(YearLevel.FIRST_YEAR);
        when(profiles.findByUserId(8L)).thenReturn(Optional.of(profile));
        when(module.getWeek()).thenReturn(week); when(week.getSubject()).thenReturn(subject);
        when(module.getStatus()).thenReturn(LessonStatus.APPROVED);
        when(module.getAiGenerationStatus()).thenReturn(AiGenerationStatus.COMPLETED);
    }
    void denied(int status, String reason, Runnable action) {
        var error = assertThrows(ResponseStatusException.class, action::run);
        assertEquals(status, error.getStatusCode().value()); assertEquals(reason, error.getReason());
    }
    @Test void approvedAndCompletedIsAccessible() { assertDoesNotThrow(() -> access.validatePublished(module, student)); }
    @Test void wrongYearIsForbidden() { subject.setYearLevel(YearLevel.SECOND_YEAR); denied(403, "STUDENT_NOT_ENROLLED", () -> access.validatePublished(module, student)); }
    @Test void missingProfileIsForbidden() { when(profiles.findByUserId(8L)).thenReturn(Optional.empty()); denied(403, "STUDENT_NOT_ENROLLED", () -> access.validatePublished(module, student)); }
    @Test void wrongRoleIsForbidden() { student.setRole(Role.PROFESSOR); denied(403, "INVALID_ROLE", () -> access.validatePublished(module, student)); }
    @Test void inactiveIsForbidden() { student.setStatus(Status.BLOCKED); denied(403, "ACCOUNT_INACTIVE", () -> access.validatePublished(module, student)); }
    @Test void unpublishedIsConflict() { when(module.getStatus()).thenReturn(LessonStatus.PENDING); denied(409, "LESSON_NOT_APPROVED", () -> access.validatePublished(module, student)); }
    @Test void incompleteGenerationIsConflict() { when(module.getAiGenerationStatus()).thenReturn(AiGenerationStatus.PENDING); denied(409, "LESSON_NOT_GENERATED", () -> access.validatePublished(module, student)); }
    @Test void missingAuthenticationIsUnauthorized() { denied(401, "AUTHENTICATION_REQUIRED", () -> access.requireStudent(null)); }
    @Test void firstWeekStillChecksSubjectAndPublication() {
        var weeks = mock(WeekRepository.class); var modules = mock(AiLearningModuleRepository.class);
        var progress = mock(StudentModuleProgressRepository.class);
        var service = new StudentLearningProgressionService(weeks, mock(SubjectRepository.class), modules, progress, access);
        when(modules.findById(15L)).thenReturn(Optional.of(module));
        when(week.getId()).thenReturn(4L);
        when(weeks.findBySubject_IdOrderByWeekNumberAsc(null)).thenReturn(List.of(week));
        assertDoesNotThrow(() -> service.validateModuleAccess(15L, student));
        subject.setYearLevel(YearLevel.SECOND_YEAR);
        denied(403, "STUDENT_NOT_ENROLLED", () -> service.validateModuleAccess(15L, student));
        denied(404, "MODULE_NOT_FOUND", () -> service.validateModuleAccess(999L, student));
    }
}
