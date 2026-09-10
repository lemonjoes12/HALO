package com.ptc.halo.service;

import com.ptc.halo.dtoRequest.StudentAnswerRequest;
import com.ptc.halo.dtoResponse.*;
import com.ptc.halo.entity.*;
import com.ptc.halo.enums.ActivityType;
import com.ptc.halo.enums.AssessmentStatus;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentQuestionRepository questionRepository;
    private final AiLearningModuleRepository moduleRepository;
    private final AssessmentAiService assessmentAiService;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final StudentModuleProgressRepository studentModuleProgressRepository;
    private final BadgeService badgeService;
    private final ActivityLogService activityLogService;

    public AssessmentService(
            AssessmentRepository assessmentRepository,
            AssessmentQuestionRepository questionRepository,
            AiLearningModuleRepository moduleRepository,
            AssessmentAiService assessmentAiService, AssessmentAttemptRepository assessmentAttemptRepository, StudentAnswerRepository studentAnswerRepository, StudentModuleProgressRepository studentModuleProgressRepository, BadgeService badgeService, ActivityLogService activityLogService) {

        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.moduleRepository = moduleRepository;
        this.assessmentAiService = assessmentAiService;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.studentModuleProgressRepository = studentModuleProgressRepository;
        this.badgeService = badgeService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public AssessmentEntity generateAssessment(Long moduleId) {

        AiLearningModuleEntity module =
                moduleRepository.findById(moduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Learning module not found"
                                )
                        );

        if (module.getStatus() != LessonStatus.APPROVED) {
            throw new RuntimeException(
                    "Assessment can only be generated from an approved lesson"
            );
        }

        if (assessmentRepository
                .findByModuleId(moduleId)
                .isPresent()) {

            throw new RuntimeException(
                    "Assessment already exists for this module"
            );
        }

        GeneratedAssessment generated =
                assessmentAiService.generateAssessment(
                        module.getLessonText(),
                        module.getGeneratedObjectives(),
                        module.getGeneratedKnowledge(),
                        module.getGeneratedExamples(),
                        module.getGeneratedSummary()
                );

        if (generated == null ||
                generated.getQuestions() == null ||
                generated.getQuestions().isEmpty()) {

            throw new RuntimeException(
                    "AI did not generate any assessment questions"
            );
        }

        AssessmentEntity assessment =
                new AssessmentEntity();

        assessment.setModule(module);
        assessment.setTitle("Module Assessment");
        assessment.setPassingScore(70);
        assessment.setStatus(AssessmentStatus.AVAILABLE);
        assessment.setCreatedAt(LocalDateTime.now());

        AssessmentEntity savedAssessment =
                assessmentRepository.save(assessment);

        for (GeneratedAssessmentQuestion generatedQuestion
                : generated.getQuestions()) {

            AssessmentQuestionEntity question =
                    new AssessmentQuestionEntity();

            question.setAssessment(savedAssessment);

            question.setQuestionNumber(
                    generatedQuestion.getQuestionNumber()
            );

            question.setQuestionText(
                    generatedQuestion.getQuestion()
            );

            question.setOptionA(
                    generatedQuestion.getOptionA()
            );

            question.setOptionB(
                    generatedQuestion.getOptionB()
            );

            question.setOptionC(
                    generatedQuestion.getOptionC()
            );

            question.setOptionD(
                    generatedQuestion.getOptionD()
            );

            question.setCorrectAnswer(
                    generatedQuestion.getCorrectAnswer()
            );

            questionRepository.save(question);
        }

        return savedAssessment;
    }

    @Transactional(readOnly = true)
    public AssessmentResponse getAssessment(Long moduleId) {

        AssessmentEntity assessment =
                assessmentRepository.findByModuleId(moduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found"
                                )
                        );

        if (assessment.getStatus() != AssessmentStatus.AVAILABLE) {
            throw new RuntimeException(
                    "Assessment is not available"
            );
        }

        List<AssessmentQuestionResponse> questions =
                assessment.getQuestions()
                        .stream()
                        .map(question -> {

                            AssessmentQuestionResponse response =
                                    new AssessmentQuestionResponse();

                            response.setId(question.getId());
                            response.setQuestionNumber(
                                    question.getQuestionNumber()
                            );
                            response.setQuestionText(
                                    question.getQuestionText()
                            );
                            response.setOptionA(
                                    question.getOptionA()
                            );
                            response.setOptionB(
                                    question.getOptionB()
                            );
                            response.setOptionC(
                                    question.getOptionC()
                            );
                            response.setOptionD(
                                    question.getOptionD()
                            );

                            return response;
                        })
                        .toList();

        AssessmentResponse response =
                new AssessmentResponse();

        response.setId(assessment.getId());
        response.setTitle(assessment.getTitle());
        response.setPassingScore(
                assessment.getPassingScore()
        );
        response.setQuestions(questions);

        return response;
    }
    public AssessmentAttemptEntity startAttempt(
            Long moduleId,
            UserEntity student) {

        AssessmentEntity assessment =
                assessmentRepository.findByModuleId(moduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found"
                                )
                        );

        if (assessment.getStatus() != AssessmentStatus.AVAILABLE) {
            throw new RuntimeException(
                    "Assessment is not available"
            );
        }

        List<AssessmentAttemptEntity> attempts =
                assessmentAttemptRepository
                        .findByStudentIdAndAssessmentIdOrderByStartedAtDesc(
                                student.getId(),
                                assessment.getId()
                        );

        // Check if student already passed
        boolean alreadyPassed =
                attempts.stream()
                        .anyMatch(attempt ->
                                Boolean.TRUE.equals(
                                        attempt.getPassed()
                                )
                        );

        if (alreadyPassed) {
            throw new RuntimeException(
                    "Assessment already passed"
            );
        }

        // Resume unfinished attempt
        for (AssessmentAttemptEntity attempt : attempts) {

            if (attempt.getSubmittedAt() == null) {
                return attempt;
            }
        }

        // No unfinished attempt -> create new attempt
        AssessmentAttemptEntity attempt =
                new AssessmentAttemptEntity();

        attempt.setAssessment(assessment);
        attempt.setStudent(student);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setSubmittedAt(null);
        attempt.setScore(0);
        attempt.setPassed(false);

        return assessmentAttemptRepository.save(attempt);
    }
    @Transactional
    public AssessmentAttemptEntity submitAttempt(
            Long attemptId,
            UserEntity student,
            StudentAnswerRequest request) {

        AssessmentAttemptEntity attempt =
                assessmentAttemptRepository.findById(attemptId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment attempt not found"
                                )
                        );

        if (!attempt.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException(
                    "This assessment attempt does not belong to the student"
            );
        }

        if (attempt.getSubmittedAt() != null) {
            throw new RuntimeException(
                    "Assessment attempt has already been submitted"
            );
        }

        if (request == null ||
                request.getAnswers() == null ||
                request.getAnswers().isEmpty()) {

            throw new RuntimeException(
                    "No answers were submitted"
            );
        }

        int correctAnswers = 0;

        for (StudentAnswerRequest.AnswerItem item
                : request.getAnswers()) {

            AssessmentQuestionEntity question =
                    questionRepository.findById(
                            item.getQuestionId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Question not found: "
                                            + item.getQuestionId()
                            )
                    );

            if (!question.getAssessment().getId()
                    .equals(attempt.getAssessment().getId())) {

                throw new RuntimeException(
                        "Question does not belong to this assessment"
                );
            }

            if (item.getAnswer() == null ||
                    item.getAnswer().isBlank()) {

                throw new RuntimeException(
                        "Answer cannot be empty for question: "
                                + item.getQuestionId()
                );
            }

            String studentAnswer =
                    item.getAnswer()
                            .trim()
                            .toUpperCase();

            if (!studentAnswer.matches("[ABCD]")) {

                throw new RuntimeException(
                        "Invalid answer for question: "
                                + item.getQuestionId()
                );
            }

            StudentAnswerEntity answer =
                    new StudentAnswerEntity();

            answer.setAttempt(attempt);
            answer.setQuestion(question);
            answer.setAnswer(studentAnswer);

            studentAnswerRepository.save(answer);

            if (question.getCorrectAnswer()
                    .equalsIgnoreCase(studentAnswer)) {

                correctAnswers++;
            }
        }

        int totalQuestions =
                attempt.getAssessment()
                        .getQuestions()
                        .size();

        int score =
                (int) Math.round(
                        ((double) correctAnswers
                                / totalQuestions) * 100
                );

        boolean passed =
                score >= attempt.getAssessment()
                        .getPassingScore();

        attempt.setScore(score);
        attempt.setPassed(passed);
        attempt.setSubmittedAt(LocalDateTime.now());

        if (passed) {

            AiLearningModuleEntity module =
                    attempt.getAssessment().getModule();

            StudentModuleProgressEntity progress =
                    studentModuleProgressRepository
                            .findByStudentIdAndModuleId(
                                    student.getId(),
                                    module.getId()
                            )
                            .orElseGet(() -> {

                                StudentModuleProgressEntity newProgress =
                                        new StudentModuleProgressEntity();

                                newProgress.setStudent(student);
                                newProgress.setModule(module);

                                return newProgress;
                            });

            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());

            studentModuleProgressRepository.save(progress);
            
            activityLogService.createLog(
                    student,
                    ActivityType.PROGRESS,
                    "Completed learning module ID " + module.getId()
            );
        }

        AssessmentAttemptEntity savedAttempt =
                assessmentAttemptRepository.save(attempt);


        if (passed) {

            activityLogService.createLog(
                    student,
                    ActivityType.ASSESSMENT,
                    "Passed module assessment with score " + score + "%"
            );

        } else {

            activityLogService.createLog(
                    student,
                    ActivityType.ASSESSMENT,
                    "Failed module assessment with score " + score + "%"
            );
        }


        badgeService.checkAndAwardBadges(
                student,
                savedAttempt
        );

        return savedAttempt;

    }

    public List<AssessmentAttemptHistoryResponse> getAttemptHistory(
            Long moduleId,
            UserEntity student) {

        AssessmentEntity assessment =
                assessmentRepository.findByModuleId(moduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found"
                                )
                        );

        List<AssessmentAttemptEntity> attempts =
                assessmentAttemptRepository
                        .findByStudentIdAndAssessmentIdOrderByStartedAtDesc(
                                student.getId(),
                                assessment.getId()
                        );

        return attempts.stream()
                .map(attempt -> {

                    AssessmentAttemptHistoryResponse response =
                            new AssessmentAttemptHistoryResponse();

                    response.setAttemptId(attempt.getId());
                    response.setScore(attempt.getScore());
                    response.setPassed(attempt.getPassed());
                    response.setStartedAt(attempt.getStartedAt());
                    response.setSubmittedAt(attempt.getSubmittedAt());

                    return response;
                })
                .toList();
    }
    public AssessmentResultResponse getAttemptResult(
            Long attemptId,
            UserEntity student) {

        AssessmentAttemptEntity attempt =
                assessmentAttemptRepository.findById(attemptId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment attempt not found"
                                )
                        );

        if (!attempt.getStudent().getId()
                .equals(student.getId())) {

            throw new RuntimeException(
                    "You cannot view this assessment attempt"
            );
        }

        if (attempt.getSubmittedAt() == null) {

            throw new RuntimeException(
                    "Assessment has not been submitted yet"
            );
        }

        List<StudentAnswerEntity> answers =
                studentAnswerRepository
                        .findByAttemptIdOrderByQuestionQuestionNumberAsc(
                                attemptId
                        );

        AssessmentResultResponse response = getAssessmentResultResponse(answers, attempt);

        return response;
    }

    @NotNull
    private static AssessmentResultResponse getAssessmentResultResponse(List<StudentAnswerEntity> answers, AssessmentAttemptEntity attempt) {
        List<AssessmentAnswerFeedbackResponse> feedback =
                new ArrayList<>();

        for (StudentAnswerEntity answer : answers) {

            AssessmentQuestionEntity question =
                    answer.getQuestion();

            AssessmentAnswerFeedbackResponse item =
                    new AssessmentAnswerFeedbackResponse();

            item.setQuestionId(question.getId());
            item.setQuestionNumber(
                    question.getQuestionNumber()
            );

            item.setQuestionText(
                    question.getQuestionText()
            );

            item.setStudentAnswer(
                    answer.getAnswer()
            );

            item.setCorrectAnswer(
                    question.getCorrectAnswer()
            );

            item.setCorrect(
                    question.getCorrectAnswer()
                            .equalsIgnoreCase(
                                    answer.getAnswer()
                            )
            );

            feedback.add(item);
        }

        AssessmentResultResponse response =
                new AssessmentResultResponse();

        response.setAttemptId(attempt.getId());
        response.setScore(attempt.getScore());
        response.setPassed(attempt.getPassed());
        response.setFeedback(feedback);
        return response;
    }
    public AssessmentStatusResponse getAssessmentStatus(
            Long moduleId,
            UserEntity student) {

        AssessmentStatusResponse response =
                new AssessmentStatusResponse();

        response.setModuleId(moduleId);

        Optional<AssessmentEntity> assessmentOptional =
                assessmentRepository.findByModuleId(moduleId);

        if (assessmentOptional.isEmpty()) {

            response.setAssessmentExists(false);
            response.setAssessmentAvailable(false);
            response.setHasUnfinishedAttempt(false);
            response.setAlreadyPassed(false);
            response.setCanTakeAssessment(false);

            return response;
        }

        AssessmentEntity assessment =
                assessmentOptional.get();

        response.setAssessmentExists(true);

        boolean available =
                assessment.getStatus()
                        == AssessmentStatus.AVAILABLE;

        response.setAssessmentAvailable(available);

        List<AssessmentAttemptEntity> attempts =
                assessmentAttemptRepository
                        .findByStudentIdAndAssessmentIdOrderByStartedAtDesc(
                                student.getId(),
                                assessment.getId()
                        );

        boolean unfinished =
                attempts.stream()
                        .anyMatch(attempt ->
                                attempt.getSubmittedAt() == null
                        );

        boolean passed =
                attempts.stream()
                        .anyMatch(attempt ->
                                Boolean.TRUE.equals(
                                        attempt.getPassed()
                                )
                        );

        response.setHasUnfinishedAttempt(unfinished);
        response.setAlreadyPassed(passed);

        response.setCanTakeAssessment(
                available && !passed
        );

        return response;
    }
}