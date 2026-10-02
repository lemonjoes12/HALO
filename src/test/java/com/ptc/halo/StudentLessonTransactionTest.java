package com.ptc.halo;

import com.ptc.halo.controller.StudentAiLearningController;
import com.ptc.halo.entity.*;
import com.ptc.halo.repository.*;
import com.ptc.halo.service.*;
import java.util.List;
import java.util.Optional;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.transaction.*;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentLessonTransactionTest {
    // Exercises the real annotation/advice boundary without connecting to production DB.
    static class TestTransactions implements PlatformTransactionManager {
        public TransactionStatus getTransaction(TransactionDefinition definition) {
            assertTrue(definition.isReadOnly());
            TransactionSynchronizationManager.setActualTransactionActive(true);
            return new SimpleTransactionStatus();
        }
        public void commit(TransactionStatus status) { TransactionSynchronizationManager.clear(); }
        public void rollback(TransactionStatus status) { TransactionSynchronizationManager.clear(); }
    }
    @SuppressWarnings("unchecked")
    <T> T transactional(T target) {
        var factory = new ProxyFactory(target);
        factory.setProxyTargetClass(true);
        factory.addAdvice(new TransactionInterceptor(new TestTransactions(), new AnnotationTransactionAttributeSource()));
        return (T) factory.getProxy();
    }
    void requireSession() {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new LazyInitializationException("Relationship accessed after repository session closed");
        }
    }
    @Test void lessonReadKeepsTransactionThroughValidationAndDtoMapping() {
        var modules = mock(AiLearningModuleRepository.class);
        var access = mock(StudentLessonAccessService.class);
        var progression = mock(StudentLearningProgressionService.class);
        var student = new UserEntity();
        var module = mock(AiLearningModuleEntity.class);
        var week = mock(WeekEntity.class);
        when(modules.findByWeekId(2L)).thenReturn(Optional.of(module));
        when(module.getId()).thenReturn(15L);
        when(module.getWeek()).thenAnswer(inv -> { requireSession(); return week; });
        when(week.getId()).thenReturn(2L);
        when(module.getGeneratedSummary()).thenReturn("Published summary");
        when(access.requireStudent(null)).thenReturn(student);
        doAnswer(inv -> { requireSession(); return null; }).when(progression).validateModuleAccess(15L, student);
        var target = new StudentAiLearningController(modules, access, progression);
        assertThrows(LazyInitializationException.class, () -> target.getApprovedLesson(2L, null));
        var result = transactional(target).getApprovedLesson(2L, null).getBody();
        assertNotNull(result); assertEquals(15L, result.getId());
        assertEquals(2L, result.getWeekId()); assertEquals("Published summary", result.getGeneratedSummary());
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
    }
    @Test void progressionReadKeepsSessionWhileNavigatingWeekAndSubject() {
        var weeks = mock(WeekRepository.class); var subjects = mock(SubjectRepository.class);
        var modules = mock(AiLearningModuleRepository.class);
        var progress = mock(StudentModuleProgressRepository.class);
        var access = mock(StudentLessonAccessService.class);
        var module = mock(AiLearningModuleEntity.class);
        var week = mock(WeekEntity.class); var subject = mock(SubjectEntity.class);
        var student = new UserEntity();
        when(modules.findById(15L)).thenReturn(Optional.of(module));
        when(module.getWeek()).thenAnswer(inv -> { requireSession(); return week; });
        when(week.getSubject()).thenReturn(subject); when(subject.getId()).thenReturn(3L);
        when(week.getId()).thenReturn(2L);
        when(weeks.findBySubject_IdOrderByWeekNumberAsc(3L)).thenReturn(List.of(week));
        var target = new StudentLearningProgressionService(weeks, subjects, modules, progress, access);
        assertThrows(LazyInitializationException.class, () -> target.validateModuleAccess(15L, student));
        transactional(target).validateModuleAccess(15L, student);
        verify(access).validatePublished(module, student);
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
    }
}
