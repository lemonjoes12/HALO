package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.UserSessionResponse;
import com.ptc.halo.entity.UserSessionEntity;
import com.ptc.halo.enums.SessionStatus;
import com.ptc.halo.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;

    public UserSessionService(
            UserSessionRepository userSessionRepository) {

        this.userSessionRepository = userSessionRepository;
    }

    @Transactional
    public void logout(String sessionId) {

        UserSessionEntity session =
                userSessionRepository
                        .findBySessionId(sessionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Session not found"
                                )
                        );

        if (session.getStatus()
                != SessionStatus.ACTIVE) {

            throw new RuntimeException(
                    "Session is no longer active"
            );
        }

        session.setStatus(
                SessionStatus.LOGGED_OUT
        );

        session.setLogoutTime(
                LocalDateTime.now()
        );

        userSessionRepository.save(session);
    }
    public List<UserSessionResponse> getAllSessions() {

        refreshExpiredSessions();

        return userSessionRepository
                .findAllByOrderByLoginTimeDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    public List<UserSessionResponse> getActiveSessions() {

        refreshExpiredSessions();

        return userSessionRepository
                .findByStatusOrderByLoginTimeDesc(
                        SessionStatus.ACTIVE
                )
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    public List<UserSessionResponse> getUserSessions(
            Long userId) {

        return userSessionRepository
                .findByUserIdOrderByLoginTimeDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private void refreshExpiredSessions() {

        LocalDateTime now = LocalDateTime.now();

        List<UserSessionEntity> activeSessions =
                userSessionRepository
                        .findByStatusOrderByLoginTimeDesc(
                                SessionStatus.ACTIVE
                        );

        for (UserSessionEntity session : activeSessions) {

            if (session.getExpiresAt().isBefore(now)) {

                session.setStatus(
                        SessionStatus.EXPIRED
                );
            }
        }

        userSessionRepository.saveAll(activeSessions);
    }

    private UserSessionResponse convertToResponse(
            UserSessionEntity session) {

        UserSessionResponse response =
                new UserSessionResponse();

        response.setId(session.getId());

        response.setUserId(
                session.getUser().getId()
        );

        response.setName(
                session.getUser().getName()
        );

        response.setEmail(
                session.getUser().getEmail()
        );

        response.setRole(
                session.getUser().getRole()
        );

        response.setLoginTime(
                session.getLoginTime()
        );

        response.setLastActivity(
                session.getLastActivity()
        );

        response.setLogoutTime(
                session.getLogoutTime()
        );

        response.setExpiresAt(
                session.getExpiresAt()
        );

        response.setStatus(
                session.getStatus()
        );

        return response;
    }
}