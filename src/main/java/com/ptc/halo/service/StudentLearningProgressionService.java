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
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.ptc.halo.enums.AiGenerationStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StudentLearningProgressionService {
    private final StudentLessonAccessService access;

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
            StudentModuleProgressRepository studentModuleProgressRepository, StudentLessonAccessService access) {
        this.access = access;

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

        // Enforce the same subject eligibility used by the student subject list.
        var subject = subjectRepository.findById(subjectId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "SUBJECT_NOT_FOUND")
                );


        access.validateSubject(student, subject);

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
                            ).filter(module -> module.getAiGenerationStatus() == AiGenerationStatus.COMPLETED);


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
                        ).filter(module -> module.getAiGenerationStatus() == AiGenerationStatus.COMPLETED);


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
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "MODULE_NOT_FOUND")
                        );

        WeekEntity currentWeek =
                module.getWeek();

        access.validatePublished(module, student);

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

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MODULE_WEEK_NOT_FOUND");
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

            throw new ResponseStatusException(HttpStatus.CONFLICT, "PREVIOUS_WEEK_INCOMPLETE");
        }
    }
}