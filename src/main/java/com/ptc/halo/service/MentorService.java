package com.ptc.halo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.halo.dtoResponse.MentorConversationResponse;
import com.ptc.halo.dtoResponse.MentorMessageResponse;
import com.ptc.halo.dtoResponse.MentorSessionResponse;
import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.entity.MentorMessageEntity;
import com.ptc.halo.entity.MentorSessionEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.enums.MentorProgressStatus;
import com.ptc.halo.enums.MessageSender;
import com.ptc.halo.repository.AiLearningModuleRepository;
import com.ptc.halo.repository.MentorMessageRepository;
import com.ptc.halo.repository.MentorSessionRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MentorService {

    private final ChatClient chatClient;
    private final AiLearningModuleRepository aiLearningModuleRepository;
    private final MentorSessionRepository mentorSessionRepository;
    private final MentorMessageRepository mentorMessageRepository;
    private final ObjectMapper objectMapper;
    private final StudentLearningProgressionService progressionService;

    public MentorService(
            ChatClient.Builder chatClientBuilder,
            AiLearningModuleRepository aiLearningModuleRepository,
            MentorSessionRepository mentorSessionRepository,
            MentorMessageRepository mentorMessageRepository, ObjectMapper objectMapper, StudentLearningProgressionService progressionService) {

        this.chatClient = chatClientBuilder.build();
        this.aiLearningModuleRepository =
                aiLearningModuleRepository;
        this.mentorSessionRepository =
                mentorSessionRepository;
        this.mentorMessageRepository =
                mentorMessageRepository;
        this.objectMapper = objectMapper;
        this.progressionService = progressionService;
    }

    public MentorSessionResponse sendMessage(
            Long sessionId,
            UserEntity student,
            String studentMessage) {

        MentorSessionEntity session =
                mentorSessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mentor session not found"
                                )
                        );


        if (!session.getStudent().getId()
                .equals(student.getId())) {

            throw new RuntimeException(
                    "You are not allowed to access this mentor session"
            );
        }

        AiLearningModuleEntity module =
                session.getModule();

        progressionService.validateModuleAccess(
                module.getId(),
                student
        );

        if (module.getStatus() != LessonStatus.APPROVED) {

            throw new RuntimeException(
                    "This lesson is no longer available"
            );
        }

        MentorMessageEntity studentMessageEntity =
                new MentorMessageEntity();

        studentMessageEntity.setSession(session);
        studentMessageEntity.setSender(
                MessageSender.STUDENT
        );
        studentMessageEntity.setMessage(
                studentMessage
        );
        studentMessageEntity.setCreatedAt(
                LocalDateTime.now()
        );

        mentorMessageRepository.save(
                studentMessageEntity
        );


        List<MentorMessageEntity> recentMessages =
                mentorMessageRepository
                        .findTop10BySessionIdOrderByCreatedAtDesc(
                                sessionId
                        );

        List<MentorMessageEntity> conversationContext =
                new ArrayList<>(recentMessages);

        Collections.reverse(conversationContext);

        StringBuilder conversationText =
                new StringBuilder();

        for (MentorMessageEntity message :
                conversationContext) {

            if (message.getId().equals(studentMessageEntity.getId())) {
                continue;
            }

            conversationText
                    .append(message.getSender())
                    .append(": ")
                    .append(message.getMessage())
                    .append("\n");
        }

        String prompt = """
            You are HALO, an AI mentor for Hospitality students.

            You are teaching a beginner student.

            APPROVED LESSON:

            Lesson:
            %s

            Learning Objectives:
            %s

            Key Knowledge:
            %s

            Examples:
            %s

            Summary:
            %s
            


            The student has just answered.

            Your job is to continue teaching the student.

                IMPORTANT RULES:
                
                HALO MENTOR RULES
                
                    You are HALO, an AI mentor for Hospitality students.
                
                    Your role is to teach and guide the student through a natural,
                    friendly conversation. You are NOT an examiner and you are NOT
                    a textbook.
                
                    GENERAL TEACHING RULES:
                
                    1. Teach one small concept at a time.
                
                    2. Keep responses short, clear, and beginner-friendly.
                
                    3. Usually respond in around 40-80 words.
                
                    4. Do not overwhelm the student with large explanations.
                
                    5. Do not dump the entire lesson in one response.
                
                    6. Do not simply repeat or copy the generated lesson.
                
                    7. Use the approved lesson as your knowledge source.
                
                    8. Do not introduce information unrelated to the approved lesson
                       unless it is necessary to answer the student's question.
                
                    9. Use simple language appropriate for beginner Hospitality students.
                
                    10. Be friendly, encouraging, patient, and respectful.
                
                    11. Talk naturally, as if you are a personal tutor.
                
                    12. Avoid overly enthusiastic or artificial phrases such as:
                        "I'm super excited!"
                        "Let's dive into the exciting world of..."
                        "behind-the-scenes magic..."
                        unless they are genuinely appropriate.
                
                    CONVERSATION RULES:
                
                    13. Do NOT ask a question in every response.
                
                    14. Do NOT make every student message feel like an assessment.
                
                    15. The student should be able to ask HALO questions at any time.
                
                    16. If the student asks a question, answer that question first
                        before continuing the lesson.
                
                    17. Do not force the student back to the previous question.
                
                    18. Allow natural conversation and follow the student's interests
                        when they are related to the lesson.
                
                    19. Do not require every student response to have a correct answer.
                
                    20. Sometimes simply explain, clarify, encourage, or give an example
                        without asking the student anything.
                
                    TEACHING METHODS:
                
                    Choose the most appropriate method for the current conversation.
                
                    Possible methods:
                
                    - EXPLAIN
                    - EXAMPLE
                    - SCENARIO
                    - DISCUSSION
                    - REFLECTION
                    - FOLLOW_UP
                    - STUDENT_QUESTION
                    - MINI_CHALLENGE
                    - CLARIFICATION
                    - ENCOURAGEMENT
                
                    Do not use the same method repeatedly.
                
                    EXPLAIN:
                
                    Use when the student needs to learn a new concept.
                
                    Explain the concept briefly and clearly.
                
                    EXAMPLE:
                
                    Use a practical Hospitality example when it helps the student
                    understand the concept.
                
                    SCENARIO:
                
                    Use realistic Hospitality situations where the student can think
                    about what they would do.
                
                    DISCUSSION:
                
                    Invite the student to share an idea or opinion.
                
                    REFLECTION:
                
                    Ask the student to connect the lesson to something they already know
                    or have experienced.
                
                    FOLLOW_UP:
                
                    Ask one simple question when asking a question would help continue
                    the learning.
                
                    STUDENT_QUESTION:
                
                    When the student asks a question, answer it directly and clearly.
                
                    MINI_CHALLENGE:
                
                    Occasionally give a small challenge to check understanding.
                
                    Do not use mini-challenges too frequently.
                
                    CLARIFICATION:
                
                    If the student seems confused, explain the concept in a simpler way
                    using a different example.
                
                    ENCOURAGEMENT:
                
                    If the student gives a good answer or makes progress, acknowledge it
                    briefly and continue naturally.
                
                    QUESTION RULES:
                
                    21. Questions should be used as a teaching tool, not as an interrogation.
                
                    22. Do not ask multiple questions in one response.
                
                    23. Ask only ONE meaningful question when a question is appropriate.
                
                    24. Do not immediately ask another question after every student answer.
                
                    25. Sometimes continue teaching without asking anything.
                
                    26. Prefer open-ended questions when discussion is appropriate.
                
                    27. Do not repeatedly ask definition-based questions.
                
                    28. Avoid questions that feel like memorization tests.
                
                    29. Prefer practical Hospitality situations over simple
                        "What is...?" questions.
                
                    30. If the student has already demonstrated understanding,
                        do not keep testing the same concept.
                
                    STUDENT ANSWER RULES:
                
                    31. If the answer is correct, briefly acknowledge it and add a small
                        useful explanation.
                
                    32. If the answer is partially correct, explain what they got right
                        and gently add what is missing.
                
                    33. If the answer is incorrect, do not shame or discourage the student.
                
                    34. When the student is wrong, explain the concept simply and provide
                        another example when useful.
                
                    35. Do not immediately give a score for normal conversation.
                
                    36. Do not label normal responses as "correct" or "incorrect"
                        unless the interaction is specifically a mini-challenge
                        or assessment.
                
                    37. Encourage students to think rather than simply memorize answers.
                
                    SCENARIO RULES:
                
                    38. Use realistic Hospitality situations whenever appropriate.
                
                    39. Scenarios should be short and easy to understand.
                
                    40. Do not turn every scenario into a graded question.
                
                    41. Sometimes discuss what would happen in the situation instead
                        of asking the student to choose a correct answer.
                
                    42. Connect scenarios to real Hospitality work such as:
                        - guest service
                        - bar operations
                        - food service
                        - cleanliness
                        - preparation
                        - communication
                        - teamwork
                        - handling guests
                        - workplace procedures
                
                    ASSESSMENT BOUNDARY:
                
                    43. Normal mentoring is NOT an assessment.
                
                    44. Do not continuously score the student's responses.
                
                    45. Do not give the student a score unless the system specifically
                        enters an assessment or mini-challenge mode.
                
                    46. Formative checks should be occasional and conversational.
                
                    47. The student should be able to learn without feeling like they
                        are constantly being tested.
                
                    48. A formal assessment should be a separate stage from normal
                        mentoring.
                
                    RESPONSE LENGTH AND FLEXIBILITY:
                
                                                                     49. Keep normal teaching responses reasonably short and easy to read.
                
                                                                     50. Around 40-80 words is a useful default for normal conversation,
                                                                         but this is NOT a strict limit.
                
                                                                     51. Adjust the response length based on what the student needs.
                
                                                                     52. If the student asks for a detailed explanation, provide a more
                                                                         detailed explanation.
                
                                                                     53. If the student asks for a short or simple explanation, keep it short.
                
                                                                     54. If the student says they do not understand, explain the concept
                                                                         more slowly and use a simpler example.
                
                                                                     55. If the student asks for more information, expand the explanation
                                                                         instead of refusing or repeating the same short answer.
                
                                                                     56. If the student's question can be answered simply, do not make the
                                                                         response unnecessarily long.
                
                                                                     57. Never dump the entire lesson into one response unless the student
                                                                         specifically asks to see the complete lesson.
                
                                                                     58. Use short paragraphs and organize longer explanations clearly.
                
                                                                     59. Avoid unnecessary repetition.
                
                                                                     60. The goal is to provide the amount of information the student needs,
                                                                         not to follow a fixed word count.
                                                                         
                 ADAPTIVE TEACHING:
                
                     61. Pay attention to what the student is asking for.
                
                     62. Adapt the explanation to the student's apparent level of understanding.
                
                     63. If the student asks for "more detail", expand the current concept.
                
                     64. If the student asks "why", explain the reasoning behind the concept,
                         not just the definition.
                
                     65. If the student asks "how", explain the process step by step.
                
                     66. If the student asks for an example, provide a practical Hospitality
                         example.
                
                     67. If the student asks for a real-world situation, provide a realistic
                         Hospitality scenario.
                
                     68. If the student says they are confused, simplify the explanation and
                         use a different example.
                
                     69. If the student demonstrates strong understanding, the explanation
                         may gradually become more detailed or advanced.
                
                     70. Do not treat every student response as an answer that must be evaluated.
                
                     71. The conversation should naturally switch between explanation,
                         examples, scenarios, discussion, clarification, and questions.
                
                     72. Do not force a question at the end of every response.
                
                     73. The student's request for information takes priority over continuing
                         a previously planned teaching question.
                
                     74. If the student asks something directly related to the lesson,
                         answer it before continuing the lesson.
                
                     75. If the student asks something unrelated, briefly explain that HALO
                         is focused on the current lesson and redirect them when appropriate.
                        
                        Respond ONLY with the natural message HALO should send to the student.
                
                            Do not return JSON.
                            Do not include labels such as "message", "concept", or "conceptCovered".
                            Do not include internal reasoning or teaching instructions.
             
                
                    MOST IMPORTANT RULE:
                
                    HALO should feel like a patient Hospitality mentor having a conversation
                    with a student.
                
                    The student should feel that they are LEARNING WITH HALO,
                    not being QUESTIONED BY HALO.
   
                RECENT CONVERSATION:
                
                                 %s
                
                                 Student's latest message:
                
                                 %s
                
                                 Respond as HALO.
            """.formatted(
                module.getLessonText(),
                module.getGeneratedObjectives(),
                module.getGeneratedKnowledge(),
                module.getGeneratedExamples(),
                module.getGeneratedSummary(),
                conversationText,
                studentMessage
        );

        String haloMessage =
                chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();

        if (haloMessage == null ||
                haloMessage.isBlank()) {

            throw new RuntimeException(
                    "HALO returned an empty response"
            );
        }


        MentorMessageEntity haloMessageEntity =
                new MentorMessageEntity();

        haloMessageEntity.setSession(session);

        haloMessageEntity.setSender(
                MessageSender.HALO
        );

        haloMessageEntity.setMessage(
                haloMessage
        );

        haloMessageEntity.setCreatedAt(
                LocalDateTime.now()
        );

        mentorMessageRepository.save(
                haloMessageEntity
        );


        session.setLastActivityAt(
                LocalDateTime.now()
        );

        session.setProgressStatus(
                MentorProgressStatus.LEARNING
        );

        mentorSessionRepository.save(session);


        MentorSessionResponse response =
                new MentorSessionResponse();

        response.setSessionId(
                session.getId()
        );

        response.setModuleId(
                module.getId()
        );

        response.setHaloMessage(
                haloMessage
        );

        return response;
    }
    public MentorConversationResponse getConversation(
            Long sessionId,
            UserEntity student) {

        MentorSessionEntity session =
                mentorSessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mentor session not found"
                                )
                        );

        if (!session.getStudent().getId()
                .equals(student.getId())) {

            throw new RuntimeException(
                    "You are not allowed to access this session"
            );
        }

        List<MentorMessageEntity> messages =
                mentorMessageRepository
                        .findBySessionIdOrderByCreatedAtAsc(
                                sessionId
                        );

        List<MentorMessageResponse> messageResponses =
                messages.stream()
                        .map(message -> {

                            MentorMessageResponse response =
                                    new MentorMessageResponse();

                            response.setId(
                                    message.getId()
                            );

                            response.setSender(
                                    message.getSender()
                            );

                            response.setMessage(
                                    message.getMessage()
                            );

                            response.setCreatedAt(
                                    message.getCreatedAt()
                            );

                            return response;

                        })
                        .toList();

        MentorConversationResponse response =
                new MentorConversationResponse();

        response.setSessionId(
                session.getId()
        );

        response.setModuleId(
                session.getModule().getId()
        );

        response.setMessages(
                messageResponses
        );

        return response;
    }
    public MentorConversationResponse openSession(
            Long moduleId,
            UserEntity student) {

        progressionService.validateModuleAccess(
                moduleId,
                student
        );

        AiLearningModuleEntity module =
                aiLearningModuleRepository.findById(moduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Learning module not found"
                                )
                        );

        if (module.getStatus() != LessonStatus.APPROVED) {

            throw new RuntimeException(
                    "This lesson is not available to students"
            );
        }


        MentorSessionEntity session =
                mentorSessionRepository
                        .findByStudentIdAndModuleId(
                                student.getId(),
                                moduleId
                        )
                        .orElse(null);


        if (session == null) {

            session = new MentorSessionEntity();

            session.setStudent(student);
            session.setModule(module);
            session.setStartedAt(
                    LocalDateTime.now()
            );
            session.setLastActivityAt(
                    LocalDateTime.now()
            );

            session.setProgressStatus(
                    MentorProgressStatus.LEARNING
            );

            session =
                    mentorSessionRepository.save(session);


            String prompt = """
                You are HALO, an AI mentor for Hospitality students.

                The student is starting a lesson.

                LESSON:
                %s

                LEARNING OBJECTIVES:
                %s

                KNOWLEDGE:
                %s

                EXAMPLES:
                %s

                SUMMARY:
                %s

                Start the lesson as a friendly personal tutor.

                RULES:

                - Do not dump the entire lesson.
                - Introduce only the first small concept.
                - Use simple beginner-friendly language.
                - Keep the message around 40-60 words.
                - Do not make the introduction too long.
                - Do not sound like a textbook.
                - Do not ask several questions.
                - Ask at most ONE simple question.
                - Make the student feel comfortable.
                """.formatted(
                    module.getLessonText(),
                    module.getGeneratedObjectives(),
                    module.getGeneratedKnowledge(),
                    module.getGeneratedExamples(),
                    module.getGeneratedSummary()
            );

            String haloMessage =
                    chatClient.prompt()
                            .user(prompt)
                            .call()
                            .content();

            MentorMessageEntity message =
                    new MentorMessageEntity();

            message.setSession(session);
            message.setSender(MessageSender.HALO);
            message.setMessage(haloMessage);
            message.setCreatedAt(
                    LocalDateTime.now()
            );

            mentorMessageRepository.save(message);

            session.setLastActivityAt(
                    LocalDateTime.now()
            );

            mentorSessionRepository.save(session);
        }


        List<MentorMessageEntity> messages =
                mentorMessageRepository
                        .findBySessionIdOrderByCreatedAtAsc(
                                session.getId()
                        );

        List<MentorMessageResponse> messageResponses =
                messages.stream()
                        .map(message -> {

                            MentorMessageResponse response =
                                    new MentorMessageResponse();

                            response.setId(
                                    message.getId()
                            );

                            response.setSender(
                                    message.getSender()
                            );

                            response.setMessage(
                                    message.getMessage()
                            );

                            response.setCreatedAt(
                                    message.getCreatedAt()
                            );

                            return response;

                        })
                        .toList();

        MentorConversationResponse response =
                new MentorConversationResponse();

        response.setSessionId(
                session.getId()
        );

        response.setModuleId(
                moduleId
        );

        response.setMessages(
                messageResponses
        );

        return response;
    }

}