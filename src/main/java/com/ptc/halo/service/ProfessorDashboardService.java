package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.ProfessorDashboardResponse;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.enums.Role;
import com.ptc.halo.repository.AiLearningModuleRepository;
import com.ptc.halo.repository.AssessmentAttemptRepository;
import com.ptc.halo.repository.AssessmentRepository;
import com.ptc.halo.repository.SubjectRepository;
import com.ptc.halo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfessorDashboardService {

    private final UserRepository userRepository;

    private final SubjectRepository subjectRepository;

    private final AiLearningModuleRepository
            aiLearningModuleRepository;

    private final AssessmentRepository
            assessmentRepository;

    private final AssessmentAttemptRepository
            assessmentAttemptRepository;


    public ProfessorDashboardService(
            UserRepository userRepository,
            SubjectRepository subjectRepository,
            AiLearningModuleRepository aiLearningModuleRepository,
            AssessmentRepository assessmentRepository,
            AssessmentAttemptRepository assessmentAttemptRepository) {

        this.userRepository =
                userRepository;

        this.subjectRepository =
                subjectRepository;

        this.aiLearningModuleRepository =
                aiLearningModuleRepository;

        this.assessmentRepository =
                assessmentRepository;

        this.assessmentAttemptRepository =
                assessmentAttemptRepository;
    }


    public ProfessorDashboardResponse
    getDashboard() {

        long totalStudents =
                userRepository
                        .countByRole(Role.STUDENT);


        long totalSubjects =
                subjectRepository.count();


        long totalModules =
                aiLearningModuleRepository.count();


        long approvedModules =
                aiLearningModuleRepository
                        .countByStatus(
                                LessonStatus.APPROVED
                        );


        long totalAssessments =
                assessmentRepository.count();


        long totalPassedAttempts =
                assessmentAttemptRepository
                        .countByPassedTrue();


        ProfessorDashboardResponse response =
                new ProfessorDashboardResponse();


        response.setTotalStudents(
                totalStudents
        );

        response.setTotalSubjects(
                totalSubjects
        );

        response.setTotalModules(
                totalModules
        );

        response.setApprovedModules(
                approvedModules
        );

        response.setTotalAssessments(
                totalAssessments
        );

        response.setTotalPassedAttempts(
                totalPassedAttempts
        );


        return response;
    }
}