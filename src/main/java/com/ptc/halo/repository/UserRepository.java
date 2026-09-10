package com.ptc.halo.repository;

import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.Role;
import com.ptc.halo.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
        boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByRole(Role role);

    boolean existsByRole(Role role);

    long countByRole(Role role);

    long countByStatus(Status status);
}
