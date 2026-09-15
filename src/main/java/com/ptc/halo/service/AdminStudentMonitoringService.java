package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.AdminStudentMonitoringResponse;
import com.ptc.halo.entity.AssessmentAttemptEntity;
import com.ptc.halo.entity.StudentProfileEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.Role;
import com.ptc.halo.repository.AssessmentAttemptRepository;
import com.ptc.halo.repository.StudentBadgeRepository;
import com.ptc.halo.repository.StudentModuleProgressRepository;
import com.ptc.halo.repository.StudentProfileRepository;
import com.ptc.halo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminStudentMonitoringService {

    private final UserRepository userRepository;

    private final StudentProfileRepository
            studentProfileRepository;

    private final StudentModuleProgressRepository
            studentModuleProgressRepository;

    private final AssessmentAttemptRepository
            assessmentAttemptRepository;

    private final StudentBadgeRepository
            studentBadgeRepository;


    public AdminStudentMonitoringService(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            StudentModuleProgressRepository studentModuleProgressRepository,
            AssessmentAttemptRepository assessmentAttemptRepository,
            StudentBadgeRepository studentBadgeRepository) {

        this.userRepository = userRepository;

        this.studentProfileRepository =
                studentProfileRepository;

        this.studentModuleProgressRepository =
                studentModuleProgressRepository;

        this.assessmentAttemptRepository =
                assessmentAttemptRepository;

        this.studentBadgeRepository =
                studentBadgeRepository;
    }


    public List<AdminStudentMonitoringResponse>
    getStudentMonitoring() {

        List<UserEntity> students =
                userRepository.findByRole(
                        Role.STUDENT
                );

        List<AdminStudentMonitoringResponse> responses =
                new ArrayList<>();


        for (UserEntity student : students) {

            Optional<StudentProfileEntity> profileOptional =
                    studentProfileRepository
                            .findByUserId(
                                    student.getId()
                            );

            if (profileOptional.isEmpty()) {
                continue;
            }

            StudentProfileEntity profile =
                    profileOptional.get();


            long completedModules =
                    studentModuleProgressRepository
                            .countByStudentIdAndCompletedTrue(
                                    student.getId()
                            );


            long passedAssessments =
                    assessmentAttemptRepository
                            .countByStudentIdAndPassedTrue(
                                    student.getId()
                            );


            long totalBadges =
                    studentBadgeRepository
                            .countByStudentId(
                                    student.getId()
                            );


            Optional<AssessmentAttemptEntity> latestAttempt =
                    assessmentAttemptRepository
                            .findTopByStudentIdAndSubmittedAtIsNotNullOrderBySubmittedAtDesc(
                                    student.getId()
                            );


            Integer latestScore =
                    latestAttempt
                            .map(
                                    AssessmentAttemptEntity::getScore
                            )
                            .orElse(null);


            AdminStudentMonitoringResponse response =
                    new AdminStudentMonitoringResponse();


            response.setUserId(
                    student.getId()
            );

            response.setStudentId(
                    profile.getStudentId()
            );

            response.setName(
                    student.getName()
            );

            response.setEmail(
                    student.getEmail()
            );

            response.setSection(
                    profile.getSection()
            );

            response.setYearLevel(
                    profile.getYearLevel()
            );

            response.setStatus(
                    student.getStatus()
            );

            response.setCompletedModules(
                    completedModules
            );

            response.setPassedAssessments(
                    passedAssessments
            );

            response.setTotalBadges(
                    totalBadges
            );

            response.setLatestAssessmentScore(
                    latestScore
            );


            responses.add(response);
        }


        return responses;
    }
}