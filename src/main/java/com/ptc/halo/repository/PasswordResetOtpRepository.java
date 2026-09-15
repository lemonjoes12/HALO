package com.ptc.halo.repository;

import com.ptc.halo.entity.PasswordResetOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtpEntity, Long> {

    Optional<PasswordResetOtpEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}