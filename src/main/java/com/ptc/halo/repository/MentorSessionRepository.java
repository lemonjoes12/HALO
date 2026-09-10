package com.ptc.halo.repository;

import com.ptc.halo.entity.MentorSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorSessionRepository
        extends JpaRepository<MentorSessionEntity, Long> {

    Optional<MentorSessionEntity> findByStudentIdAndModuleId(
            Long studentId,
            Long moduleId
    );
}