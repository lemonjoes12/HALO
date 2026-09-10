package com.ptc.halo.repository;

import com.ptc.halo.entity.MentorMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorMessageRepository
        extends JpaRepository<MentorMessageEntity, Long> {

    List<MentorMessageEntity>
    findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    List<MentorMessageEntity>
    findTop10BySessionIdOrderByCreatedAtDesc(Long sessionId);
}