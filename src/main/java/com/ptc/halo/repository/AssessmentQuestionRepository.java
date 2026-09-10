package com.ptc.halo.repository;

import com.ptc.halo.entity.AssessmentQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentQuestionRepository
        extends JpaRepository<AssessmentQuestionEntity, Long> {

    List<AssessmentQuestionEntity>
    findByAssessmentIdOrderByQuestionNumberAsc(Long assessmentId);
}