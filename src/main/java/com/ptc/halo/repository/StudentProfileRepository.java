package com.ptc.halo.repository;

import com.ptc.halo.entity.StudentProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfileEntity, Long> {
    Optional<StudentProfileEntity>
    findByUserId(Long userId);
}
