package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.StudentDashboardResponse;
import com.ptc.halo.entity.AssessmentAttemptEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.AssessmentAttemptRepository;
import com.ptc.halo.repository.StudentBadgeRepository;
import com.ptc.halo.repository.StudentModuleProgressRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentDashboardService {

    private final StudentModuleProgressRepository
            studentModuleProgressRepository;

    private final StudentBadgeRepository
            studentBadgeRepository;

    private final AssessmentAttemptRepository
            assessmentAttemptRepository;


    public StudentDashboardService(
            StudentModuleProgressRepository studentModuleProgressRepository,
            StudentBadgeRepository studentBadgeRepository,
            AssessmentAttemptRepository assessmentAttemptRepository) {

        this.studentModuleProgressRepository =
                studentModuleProgressRepository;

        this.studentBadgeRepository =
                studentBadgeRepository;

        this.assessmentAttemptRepository =
                assessmentAttemptRepository;
    }


    public StudentDashboardResponse getDashboard(
            UserEntity student) {

        StudentDashboardResponse response =
                new StudentDashboardResponse();


        long completedModules =
                studentModuleProgressRepository
                        .countByStudentIdAndCompletedTrue(
                                student.getId()
                        );


        long totalBadges =
                studentBadgeRepository
                        .countByStudentId(
                                student.getId()
                        );


        long passedAssessments =
                assessmentAttemptRepository
                        .countByStudentIdAndPassedTrue(
                                student.getId()
                        );


        Optional<AssessmentAttemptEntity> latestAttempt =
                assessmentAttemptRepository
                        .findTopByStudentIdAndSubmittedAtIsNotNullOrderBySubmittedAtDesc(
                                student.getId()
                        );


        Integer latestScore =
                latestAttempt
                        .map(AssessmentAttemptEntity::getScore)
                        .orElse(null);


        response.setCompletedModules(
                completedModules
        );

        response.setTotalBadges(
                totalBadges
        );

        response.setPassedAssessments(
                passedAssessments
        );

        response.setLatestAssessmentScore(
                latestScore
        );


        return response;
    }
}