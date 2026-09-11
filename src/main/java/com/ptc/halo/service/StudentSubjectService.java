package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.StudentSubjectResponse;
import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.entity.StudentProfileEntity;
import com.ptc.halo.entity.SubjectEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.repository.AiLearningModuleRepository;
import com.ptc.halo.repository.StudentModuleProgressRepository;
import com.ptc.halo.repository.StudentProfileRepository;
import com.ptc.halo.repository.SubjectRepository;
import com.ptc.halo.repository.WeekRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentSubjectService {

    private final SubjectRepository subjectRepository;

    private final StudentProfileRepository
            studentProfileRepository;

    private final WeekRepository weekRepository;

    private final AiLearningModuleRepository
            aiLearningModuleRepository;

    private final StudentModuleProgressRepository
            studentModuleProgressRepository;

    public StudentSubjectService(
            SubjectRepository subjectRepository,
            StudentProfileRepository studentProfileRepository,
            WeekRepository weekRepository,
            AiLearningModuleRepository aiLearningModuleRepository,
            StudentModuleProgressRepository studentModuleProgressRepository) {

        this.subjectRepository =
                subjectRepository;

        this.studentProfileRepository =
                studentProfileRepository;

        this.weekRepository =
                weekRepository;

        this.aiLearningModuleRepository =
                aiLearningModuleRepository;

        this.studentModuleProgressRepository =
                studentModuleProgressRepository;
    }


    public List<StudentSubjectResponse>
    getStudentSubjects(UserEntity student) {

        StudentProfileEntity profile =
                studentProfileRepository
                        .findByUserId(student.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student profile not found"
                                )
                        );


        List<SubjectEntity> subjects =
                subjectRepository
                        .findByYearLevel(
                                profile.getYearLevel()
                        );


        List<StudentSubjectResponse> responses =
                new ArrayList<>();


        for (SubjectEntity subject : subjects) {

            int totalWeeks =
                    weekRepository
                            .findBySubject_Id(
                                    subject.getId()
                            )
                            .size();


            List<AiLearningModuleEntity>
                    approvedModules =
                    aiLearningModuleRepository
                            .findByWeek_Subject_IdAndStatus(
                                    subject.getId(),
                                    LessonStatus.APPROVED
                            );


            int completedWeeks = 0;


            for (AiLearningModuleEntity module
                    : approvedModules) {

                boolean completed =
                        studentModuleProgressRepository
                                .findByStudentIdAndModuleId(
                                        student.getId(),
                                        module.getId()
                                )
                                .map(progress ->
                                        Boolean.TRUE.equals(
                                                progress.getCompleted()
                                        )
                                )
                                .orElse(false);


                if (completed) {
                    completedWeeks++;
                }
            }


            int progressPercentage = 0;

            if (totalWeeks > 0) {

                progressPercentage =
                        (int) Math.round(
                                ((double) completedWeeks
                                        / totalWeeks)
                                        * 100
                        );
            }


            StudentSubjectResponse response =
                    new StudentSubjectResponse();


            response.setSubjectId(
                    subject.getId()
            );

            response.setSubjectCode(
                    subject.getSubjectCode()
            );

            response.setSubjectName(
                    subject.getSubjectName()
            );

            response.setYearLevel(
                    subject.getYearLevel()
            );

            response.setTotalWeeks(
                    totalWeeks
            );

            response.setCompletedWeeks(
                    completedWeeks
            );

            response.setProgressPercentage(
                    progressPercentage
            );


            responses.add(response);
        }


        return responses;
    }
}