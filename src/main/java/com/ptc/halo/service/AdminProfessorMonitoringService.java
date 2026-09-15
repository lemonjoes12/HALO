package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.AdminProfessorMonitoringResponse;
import com.ptc.halo.entity.ActivityLogEntity;
import com.ptc.halo.entity.ProfessorEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.ActivityType;
import com.ptc.halo.enums.Role;
import com.ptc.halo.repository.ActivityLogRepository;
import com.ptc.halo.repository.ProfessorRepository;
import com.ptc.halo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminProfessorMonitoringService {

    private final UserRepository userRepository;

    private final ProfessorRepository professorRepository;

    private final ActivityLogRepository activityLogRepository;


    public AdminProfessorMonitoringService(
            UserRepository userRepository,
            ProfessorRepository professorRepository,
            ActivityLogRepository activityLogRepository) {

        this.userRepository = userRepository;
        this.professorRepository = professorRepository;
        this.activityLogRepository = activityLogRepository;
    }


    public List<AdminProfessorMonitoringResponse>
    getProfessorMonitoring() {

        List<UserEntity> professors =
                userRepository.findByRole(
                        Role.PROFESSOR
                );


        List<AdminProfessorMonitoringResponse> responses =
                new ArrayList<>();


        for (UserEntity professor : professors) {

            Optional<ProfessorEntity> profileOptional =
                    professorRepository.findByUserId(
                            professor.getId()
                    );


            if (profileOptional.isEmpty()) {
                continue;
            }


            ProfessorEntity profile =
                    profileOptional.get();


            long moduleActivities =
                    activityLogRepository
                            .countByUserIdAndActivityType(
                                    professor.getId(),
                                    ActivityType.MODULE
                            );


            Optional<ActivityLogEntity> latestActivity =
                    activityLogRepository
                            .findTopByUserIdOrderByCreatedAtDesc(
                                    professor.getId()
                            );


            LocalDateTime lastActivity =
                    latestActivity
                            .map(
                                    ActivityLogEntity::getCreatedAt
                            )
                            .orElse(null);


            AdminProfessorMonitoringResponse response =
                    new AdminProfessorMonitoringResponse();


            response.setUserId(
                    professor.getId()
            );


            response.setProfessorId(
                    profile.getProfessorId()
            );


            response.setName(
                    professor.getName()
            );


            response.setEmail(
                    professor.getEmail()
            );


            response.setStatus(
                    professor.getStatus()
            );


            response.setModuleActivities(
                    moduleActivities
            );


            response.setLastActivity(
                    lastActivity
            );


            responses.add(response);
        }


        return responses;
    }
}