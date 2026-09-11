package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.ProfessorStudentProgressResponse;
import com.ptc.halo.dtoResponse.ProfessorStudentResponse;
import com.ptc.halo.dtoResponse.StudentSubjectResponse;
import com.ptc.halo.entity.StudentProfileEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.Role;
import com.ptc.halo.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfessorStudentService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentModuleProgressRepository studentModuleProgressRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final StudentBadgeRepository studentBadgeRepository;
    private final StudentSubjectService studentSubjectService;

    public ProfessorStudentService(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository, StudentModuleProgressRepository studentModuleProgressRepository, AssessmentAttemptRepository assessmentAttemptRepository, StudentBadgeRepository studentBadgeRepository, StudentSubjectService studentSubjectService) {

        this.userRepository = userRepository;
        this.studentProfileRepository =
                studentProfileRepository;
        this.studentModuleProgressRepository = studentModuleProgressRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.studentBadgeRepository = studentBadgeRepository;
        this.studentSubjectService = studentSubjectService;
    }


    public List<ProfessorStudentResponse>
    getStudents() {

        List<UserEntity> students =
                userRepository.findByRole(Role.STUDENT);

        List<ProfessorStudentResponse> responses =
                new ArrayList<>();


        for (UserEntity student : students) {

            StudentProfileEntity profile =
                    studentProfileRepository
                            .findByUserId(student.getId())
                            .orElse(null);

            if (profile == null) {
                continue;
            }


            ProfessorStudentResponse response =
                    new ProfessorStudentResponse();

            response.setUserId(
                    student.getId()
            );

            response.setName(
                    student.getName()
            );

            response.setEmail(
                    student.getEmail()
            );

            response.setStudentId(
                    profile.getStudentId()
            );

            response.setSection(
                    profile.getSection()
            );

            response.setYearLevel(
                    profile.getYearLevel()
            );


            responses.add(response);
        }


        return responses;
    }
    public ProfessorStudentProgressResponse
    getStudentProgress(Long userId) {

        UserEntity student =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException(
                    "User is not a student"
            );
        }

        StudentProfileEntity profile =
                studentProfileRepository
                        .findByUserId(student.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student profile not found"
                                )
                        );


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


        ProfessorStudentProgressResponse response =
                new ProfessorStudentProgressResponse();

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

        response.setCompletedModules(
                completedModules
        );

        response.setPassedAssessments(
                passedAssessments
        );

        response.setTotalBadges(
                totalBadges
        );

        return response;
    }
    public List<StudentSubjectResponse>
    getStudentSubjects(Long userId) {

        UserEntity student =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException(
                    "User is not a student"
            );
        }

        return studentSubjectService
                .getStudentSubjects(student);
    }
}