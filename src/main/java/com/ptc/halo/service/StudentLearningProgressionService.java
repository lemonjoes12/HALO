package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.StudentWeekAccessResponse;
import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.entity.StudentModuleProgressEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.entity.WeekEntity;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.repository.AiLearningModuleRepository;
import com.ptc.halo.repository.StudentModuleProgressRepository;
import com.ptc.halo.repository.SubjectRepository;
import com.ptc.halo.repository.WeekRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentLearningProgressionService {

    private final WeekRepository weekRepository;

    private final SubjectRepository subjectRepository;

    private final AiLearningModuleRepository
            aiLearningModuleRepository;

    private final StudentModuleProgressRepository
            studentModuleProgressRepository;


    public StudentLearningProgressionService(
            WeekRepository weekRepository,
            SubjectRepository subjectRepository,
            AiLearningModuleRepository aiLearningModuleRepository,
            StudentModuleProgressRepository studentModuleProgressRepository) {

        this.weekRepository = weekRepository;

        this.subjectRepository = subjectRepository;

        this.aiLearningModuleRepository =
                aiLearningModuleRepository;

        this.studentModuleProgressRepository =
                studentModuleProgressRepository;
    }


    public List<StudentWeekAccessResponse> getWeekAccess(
            Long subjectId,
            UserEntity student) {

        // Make sure subject exists
        subjectRepository.findById(subjectId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Subject not found"
                        )
                );


        List<WeekEntity> weeks =
                weekRepository
                        .findBySubject_IdOrderByWeekNumberAsc(
                                subjectId
                        );


        List<StudentWeekAccessResponse> responses =
                new ArrayList<>();


        for (int i = 0; i < weeks.size(); i++) {

            WeekEntity week =
                    weeks.get(i);


            Optional<AiLearningModuleEntity> moduleOptional =
                    aiLearningModuleRepository
                            .findByWeekIdAndStatus(
                                    week.getId(),
                                    LessonStatus.APPROVED
                            );


            boolean lessonAvailable =
                    moduleOptional.isPresent();


            boolean completed = false;


            if (moduleOptional.isPresent()) {

                AiLearningModuleEntity module =
                        moduleOptional.get();


                Optional<StudentModuleProgressEntity> progressOptional =
                        studentModuleProgressRepository
                                .findByStudentIdAndModuleId(
                                        student.getId(),
                                        module.getId()
                                );


                completed =
                        progressOptional.isPresent()
                                &&
                                Boolean.TRUE.equals(
                                        progressOptional
                                                .get()
                                                .getCompleted()
                                );
            }


            boolean unlocked;


            // First week is always unlocked
            if (i == 0) {

                unlocked = true;

            } else {

                WeekEntity previousWeek =
                        weeks.get(i - 1);


                unlocked =
                        isPreviousWeekCompleted(
                                previousWeek,
                                student
                        );
            }


            StudentWeekAccessResponse response =
                    new StudentWeekAccessResponse();


            response.setWeekId(
                    week.getId()
            );

            response.setWeekNumber(
                    week.getWeekNumber()
            );

            response.setTitle(
                    week.getTitle()
            );

            response.setUnlocked(
                    unlocked
            );

            response.setCompleted(
                    completed
            );

            response.setLessonAvailable(
                    lessonAvailable
            );


            if (moduleOptional.isPresent()) {

                response.setModuleId(
                        moduleOptional
                                .get()
                                .getId()
                );

            } else {

                response.setModuleId(null);
            }


            responses.add(response);
        }


        return responses;
    }


    private boolean isPreviousWeekCompleted(
            WeekEntity previousWeek,
            UserEntity student) {


        Optional<AiLearningModuleEntity> previousModuleOptional =
                aiLearningModuleRepository
                        .findByWeekIdAndStatus(
                                previousWeek.getId(),
                                LessonStatus.APPROVED
                        );


        if (previousModuleOptional.isEmpty()) {
            return false;
        }


        AiLearningModuleEntity previousModule =
                previousModuleOptional.get();


        Optional<StudentModuleProgressEntity> progressOptional =
                studentModuleProgressRepository
                        .findByStudentIdAndModuleId(
                                student.getId(),
                                previousModule.getId()
                        );


        if (progressOptional.isEmpty()) {
            return false;
        }


        return Boolean.TRUE.equals(
                progressOptional
                        .get()
                        .getCompleted()
        );
    }
    public void validateModuleAccess(
            Long moduleId,
            UserEntity student) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository
                        .findById(moduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Learning module not found"
                                )
                        );

        WeekEntity currentWeek =
                module.getWeek();

        Long subjectId =
                currentWeek
                        .getSubject()
                        .getId();

        List<WeekEntity> weeks =
                weekRepository
                        .findBySubject_IdOrderByWeekNumberAsc(
                                subjectId
                        );

        int currentIndex = -1;

        for (int i = 0; i < weeks.size(); i++) {

            if (weeks.get(i)
                    .getId()
                    .equals(currentWeek.getId())) {

                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {

            throw new RuntimeException(
                    "Week not found in subject"
            );
        }

        // First week is always unlocked
        if (currentIndex == 0) {
            return;
        }

        WeekEntity previousWeek =
                weeks.get(currentIndex - 1);

        boolean previousCompleted =
                isPreviousWeekCompleted(
                        previousWeek,
                        student
                );

        if (!previousCompleted) {

            throw new RuntimeException(
                    "This week is locked. Complete the previous week first."
            );
        }
    }
}