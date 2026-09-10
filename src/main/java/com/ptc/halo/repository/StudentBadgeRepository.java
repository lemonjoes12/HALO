package com.ptc.halo.repository;

import com.ptc.halo.entity.StudentBadgeEntity;
import com.ptc.halo.enums.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentBadgeRepository
        extends JpaRepository<StudentBadgeEntity, Long> {

    boolean existsByStudentIdAndBadgeType(
            Long studentId,
            BadgeType badgeType
    );

    List<StudentBadgeEntity>
    findByStudentIdOrderByEarnedAtDesc(
            Long studentId
    );

    long countByStudentId(
            Long studentId
    );
}