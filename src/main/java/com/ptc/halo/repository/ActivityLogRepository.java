package com.ptc.halo.repository;

import com.ptc.halo.entity.ActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository
        extends JpaRepository<ActivityLogEntity, Long> {

}