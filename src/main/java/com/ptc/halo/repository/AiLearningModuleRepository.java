package com.ptc.halo.repository;

import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.enums.LessonStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface AiLearningModuleRepository
        extends JpaRepository<AiLearningModuleEntity, Long> {

    Optional<AiLearningModuleEntity> findByWeekId(Long weekId);

    Optional<AiLearningModuleEntity> findByWeekIdAndStatus(Long weekId, LessonStatus status);

    List<AiLearningModuleEntity>
    findByWeek_Subject_IdAndStatus(
            Long subjectId,
            LessonStatus status
    );

    @EntityGraph(attributePaths = {"files"})
    Optional<AiLearningModuleEntity> findWithFilesById(Long id);
}
