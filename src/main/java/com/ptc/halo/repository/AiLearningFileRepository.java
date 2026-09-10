package com.ptc.halo.repository;

import com.ptc.halo.entity.AiLearningFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiLearningFileRepository
        extends JpaRepository<AiLearningFileEntity, Long> {
}