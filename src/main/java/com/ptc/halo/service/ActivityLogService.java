package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.ActivityLogResponse;
import com.ptc.halo.entity.ActivityLogEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.ActivityType;
import com.ptc.halo.enums.Role;
import com.ptc.halo.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void createLog(
            UserEntity user,
            ActivityType activityType,
            String action) {

        ActivityLogEntity log =
                new ActivityLogEntity();

        log.setUser(user);

        log.setActivityType(
                activityType
        );

        log.setAction(
                action
        );

        log.setCreatedAt(
                LocalDateTime.now()
        );

        activityLogRepository.save(log);
    }
    public List<ActivityLogResponse> getAllLogs() {

        return activityLogRepository.findAll()
                .stream()
                .map(log -> {

                    ActivityLogResponse response =
                            new ActivityLogResponse();

                    response.setId(
                            log.getId()
                    );

                    response.setUserName(
                            log.getUser().getName()
                    );

                    response.setUserEmail(
                            log.getUser().getEmail()
                    );

                    response.setUserRole(
                            log.getUser().getRole()
                    );

                    response.setActivityType(
                            log.getActivityType()
                    );

                    response.setAction(
                            log.getAction()
                    );

                    response.setCreatedAt(
                            log.getCreatedAt()
                    );

                    return response;
                })
                .toList();
    }
    public List<ActivityLogResponse> getLogs(
            Role role,
            ActivityType activityType) {

        List<ActivityLogEntity> logs;

        if (role != null && activityType != null) {

            logs = activityLogRepository
                    .findByUser_RoleAndActivityTypeOrderByCreatedAtDesc(
                            role,
                            activityType
                    );

        } else if (role != null) {

            logs = activityLogRepository
                    .findByUser_RoleOrderByCreatedAtDesc(role);

        } else if (activityType != null) {

            logs = activityLogRepository
                    .findByActivityTypeOrderByCreatedAtDesc(
                            activityType
                    );

        } else {

            logs = activityLogRepository
                    .findAllByOrderByCreatedAtDesc();
        }

        return logs.stream()
                .map(log -> {

                    ActivityLogResponse response =
                            new ActivityLogResponse();

                    response.setId(log.getId());
                    response.setUserName(
                            log.getUser().getName()
                    );
                    response.setUserEmail(
                            log.getUser().getEmail()
                    );
                    response.setUserRole(
                            log.getUser().getRole()
                    );
                    response.setActivityType(
                            log.getActivityType()
                    );
                    response.setAction(
                            log.getAction()
                    );
                    response.setCreatedAt(
                            log.getCreatedAt()
                    );

                    return response;
                })
                .toList();
    }
}