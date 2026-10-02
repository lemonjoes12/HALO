package com.ptc.halo.repository;

import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.enums.LessonStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface AiLearningModuleRepository
        extends JpaRepository<AiLearningModuleEntity, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select m from AiLearningModuleEntity m where m.id = :id")
    Optional<AiLearningModuleEntity> findForMentorOpenById(@org.springframework.data.repository.query.Param("id") Long id);


    Optional<AiLearningModuleEntity> findByWeekId(Long weekId);

    Optional<AiLearningModuleEntity> findByWeekIdAndStatus(Long weekId, LessonStatus status);

    List<AiLearningModuleEntity>
    findByWeek_Subject_IdAndStatus(
            Long subjectId,
            LessonStatus status
    );

    @EntityGraph(attributePaths = {"files"})
    Optional<AiLearningModuleEntity> findWithFilesById(Long id);

    long countByStatus(LessonStatus status);
}
