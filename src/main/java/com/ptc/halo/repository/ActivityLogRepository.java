package com.ptc.halo.repository;

import com.ptc.halo.entity.ActivityLogEntity;
import com.ptc.halo.enums.ActivityType;
import com.ptc.halo.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityLogRepository
        extends JpaRepository<ActivityLogEntity, Long> {

    List<ActivityLogEntity>
    findAllByOrderByCreatedAtDesc();

    List<ActivityLogEntity>
    findByUser_RoleOrderByCreatedAtDesc(Role role);

    List<ActivityLogEntity>
    findByActivityTypeOrderByCreatedAtDesc(
            ActivityType activityType
    );

    List<ActivityLogEntity>
    findByUser_RoleAndActivityTypeOrderByCreatedAtDesc(
            Role role,
            ActivityType activityType
    );
    List<ActivityLogEntity>
    findTop10ByOrderByCreatedAtDesc();

    long countByUserIdAndActivityType(
            Long userId,
            ActivityType activityType
    );

    Optional<ActivityLogEntity>
    findTopByUserIdOrderByCreatedAtDesc(
            Long userId
    );
}