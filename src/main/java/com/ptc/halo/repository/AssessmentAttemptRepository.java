package com.ptc.halo.repository;

import com.ptc.halo.entity.AssessmentAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentAttemptRepository
        extends JpaRepository<AssessmentAttemptEntity, Long> {

    List<AssessmentAttemptEntity>
    findByStudentIdAndAssessmentIdOrderByStartedAtDesc(
            Long studentId,
            Long assessmentId
    );

    long countByStudentIdAndPassedTrue(
            Long studentId
    );
    Optional<AssessmentAttemptEntity>
    findTopByStudentIdAndSubmittedAtIsNotNullOrderBySubmittedAtDesc(
            Long studentId
    );
}