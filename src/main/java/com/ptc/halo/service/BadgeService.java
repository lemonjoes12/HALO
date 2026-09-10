package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.StudentBadgeResponse;
import com.ptc.halo.entity.AssessmentAttemptEntity;
import com.ptc.halo.entity.StudentBadgeEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.ActivityType;
import com.ptc.halo.enums.BadgeType;
import com.ptc.halo.repository.AssessmentAttemptRepository;
import com.ptc.halo.repository.StudentBadgeRepository;
import com.ptc.halo.repository.StudentModuleProgressRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BadgeService {

    private final StudentBadgeRepository studentBadgeRepository;
    private final ActivityLogService activityLogService;
    private final StudentModuleProgressRepository studentModuleProgressRepository;

    private final AssessmentAttemptRepository
            assessmentAttemptRepository;

    public BadgeService(
            StudentBadgeRepository studentBadgeRepository, ActivityLogService activityLogService,
            StudentModuleProgressRepository studentModuleProgressRepository,
            AssessmentAttemptRepository assessmentAttemptRepository) {

        this.studentBadgeRepository =
                studentBadgeRepository;
        this.activityLogService = activityLogService;

        this.studentModuleProgressRepository =
                studentModuleProgressRepository;

        this.assessmentAttemptRepository =
                assessmentAttemptRepository;
    }


    public void checkAndAwardBadges(
            UserEntity student,
            AssessmentAttemptEntity currentAttempt) {

        checkProgressBadges(student);

        checkAssessmentBadges(
                student,
                currentAttempt
        );

        checkHaloAchiever(student);
    }


    // =========================================
    // PROGRESS BADGES
    // =========================================

    private void checkProgressBadges(
            UserEntity student) {

        long completedModules =
                studentModuleProgressRepository
                        .countByStudentIdAndCompletedTrue(
                                student.getId()
                        );


        if (completedModules >= 1) {

            awardBadge(
                    student,
                    BadgeType.FIRST_STEP,
                    "First Step",
                    "Completed your first learning module."
            );
        }


        if (completedModules >= 3) {

            awardBadge(
                    student,
                    BadgeType.GETTING_STARTED,
                    "Getting Started",
                    "Completed 3 learning modules."
            );
        }


        if (completedModules >= 5) {

            awardBadge(
                    student,
                    BadgeType.KNOWLEDGE_SEEKER,
                    "Knowledge Seeker",
                    "Completed 5 learning modules."
            );
        }


        if (completedModules >= 10) {

            awardBadge(
                    student,
                    BadgeType.DEDICATED_LEARNER,
                    "Dedicated Learner",
                    "Completed 10 learning modules."
            );
        }
    }


    // =========================================
    // ASSESSMENT BADGES
    // =========================================

    private void checkAssessmentBadges(
            UserEntity student,
            AssessmentAttemptEntity currentAttempt) {


        // PERFECT SCORE
        if (currentAttempt.getScore() != null &&
                currentAttempt.getScore() == 100) {

            awardBadge(
                    student,
                    BadgeType.PERFECT_SCORE,
                    "Perfect Score",
                    "Earned a perfect score on an assessment."
            );
        }


        List<AssessmentAttemptEntity> attempts =
                assessmentAttemptRepository
                        .findByStudentIdAndAssessmentIdOrderByStartedAtDesc(
                                student.getId(),
                                currentAttempt
                                        .getAssessment()
                                        .getId()
                        );


        // FIRST TRY
        if (Boolean.TRUE.equals(
                currentAttempt.getPassed()
        )) {

            long submittedAttempts =
                    attempts.stream()
                            .filter(attempt ->
                                    attempt.getSubmittedAt()
                                            != null
                            )
                            .count();

            if (submittedAttempts == 1) {

                awardBadge(
                        student,
                        BadgeType.FIRST_TRY,
                        "First Try",
                        "Passed an assessment on the first attempt."
                );
            }
        }


        // NEVER GIVE UP
        if (Boolean.TRUE.equals(
                currentAttempt.getPassed()
        )) {

            boolean previouslyFailed =
                    attempts.stream()
                            .anyMatch(attempt ->
                                    attempt.getId()
                                            .equals(
                                                    currentAttempt.getId()
                                            ) == false
                                            &&
                                            attempt.getSubmittedAt()
                                                    != null
                                            &&
                                            Boolean.FALSE.equals(
                                                    attempt.getPassed()
                                            )
                            );

            if (previouslyFailed) {

                awardBadge(
                        student,
                        BadgeType.NEVER_GIVE_UP,
                        "Never Give Up",
                        "Passed an assessment after a previous failed attempt."
                );
            }
        }


        // COMEBACK STRONGER
        checkComebackBadge(
                student,
                currentAttempt,
                attempts
        );


        // ASSESSMENT ACE
        long passedAssessments =
                assessmentAttemptRepository
                        .countByStudentIdAndPassedTrue(
                                student.getId()
                        );

        if (passedAssessments >= 5) {

            awardBadge(
                    student,
                    BadgeType.ASSESSMENT_ACE,
                    "Assessment Ace",
                    "Passed 5 assessments."
            );
        }
    }


    // =========================================
    // COMEBACK STRONGER
    // =========================================

    private void checkComebackBadge(
            UserEntity student,
            AssessmentAttemptEntity currentAttempt,
            List<AssessmentAttemptEntity> attempts) {

        if (currentAttempt.getScore() == null) {
            return;
        }


        AssessmentAttemptEntity previousAttempt =
                attempts.stream()
                        .filter(attempt ->
                                !attempt.getId()
                                        .equals(
                                                currentAttempt.getId()
                                        )
                        )
                        .filter(attempt ->
                                attempt.getSubmittedAt()
                                        != null
                        )
                        .filter(attempt ->
                                attempt.getScore()
                                        != null
                        )
                        .findFirst()
                        .orElse(null);


        if (previousAttempt == null) {
            return;
        }


        int improvement =
                currentAttempt.getScore()
                        - previousAttempt.getScore();

        if (improvement >= 20) {

            awardBadge(
                    student,
                    BadgeType.COMEBACK_STRONGER,
                    "Comeback Stronger",
                    "Improved your assessment score by at least 20 points."
            );
        }
    }


    // =========================================
    // HALO ACHIEVER
    // =========================================

    private void checkHaloAchiever(
            UserEntity student) {

        long totalBadges =
                studentBadgeRepository
                        .countByStudentId(
                                student.getId()
                        );

        if (totalBadges >= 10) {

            awardBadge(
                    student,
                    BadgeType.HALO_ACHIEVER,
                    "HALO Achiever",
                    "Earned 10 different HALO badges."
            );
        }
    }


    // =========================================
    // SAVE BADGE
    // =========================================

    private void awardBadge(
            UserEntity student,
            BadgeType badgeType,
            String badgeName,
            String description) {

        boolean alreadyEarned =
                studentBadgeRepository
                        .existsByStudentIdAndBadgeType(
                                student.getId(),
                                badgeType
                        );


        if (alreadyEarned) {
            return;
        }


        StudentBadgeEntity badge =
                new StudentBadgeEntity();

        badge.setStudent(student);

        badge.setBadgeType(
                badgeType
        );

        badge.setBadgeName(
                badgeName
        );

        badge.setDescription(
                description
        );

        badge.setEarnedAt(
                LocalDateTime.now()
        );


        studentBadgeRepository.save(badge);

        activityLogService.createLog(
                student,
                ActivityType.BADGE,
                "Earned badge: " + badge.getBadgeName());
    }
    public List<StudentBadgeResponse> getStudentBadges(
            UserEntity student) {

        return studentBadgeRepository
                .findByStudentIdOrderByEarnedAtDesc(
                        student.getId()
                )
                .stream()
                .map(badge -> {

                    StudentBadgeResponse response =
                            new StudentBadgeResponse();

                    response.setId(
                            badge.getId()
                    );

                    response.setBadgeType(
                            badge.getBadgeType()
                    );

                    response.setBadgeName(
                            badge.getBadgeName()
                    );

                    response.setDescription(
                            badge.getDescription()
                    );

                    response.setEarnedAt(
                            badge.getEarnedAt()
                    );

                    return response;
                })
                .toList();
    }
}