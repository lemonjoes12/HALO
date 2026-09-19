package com.ptc.halo.repository;

import com.ptc.halo.entity.UserSessionEntity;
import com.ptc.halo.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository
        extends JpaRepository<UserSessionEntity, Long> {

    Optional<UserSessionEntity> findBySessionId(
            String sessionId
    );

    List<UserSessionEntity> findAllByOrderByLoginTimeDesc();

    List<UserSessionEntity>
    findByStatusOrderByLoginTimeDesc(
            SessionStatus status
    );

    List<UserSessionEntity>
    findByUserIdOrderByLoginTimeDesc(
            Long userId
    );
}