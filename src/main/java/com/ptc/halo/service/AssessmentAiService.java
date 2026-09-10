package com.ptc.halo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.halo.dtoResponse.GeneratedAssessment;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AssessmentAiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AssessmentAiService(
            ChatClient.Builder chatClientBuilder,
            ObjectMapper objectMapper) {

        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public GeneratedAssessment generateAssessment(
            String lessonText,
            String objectives,
            String knowledge,
            String examples,
            String summary) {

        String prompt = """
                You are HALO, an AI learning assessment generator
                for Hospitality and Tourism students.

                Create a multiple-choice assessment based ONLY on
                the approved lesson content provided below.

                LESSON:
                %s

                LEARNING OBJECTIVES:
                %s

                KEY KNOWLEDGE:
                %s

                EXAMPLES:
                %s

                SUMMARY:
                %s

                ASSESSMENT RULES:
                - Generate exactly 10 questions.
                - Each question must have exactly 4 choices.
                - Choices must be labeled A, B, C, and D.
                - Only one answer may be correct.
                - Questions must test understanding of the lesson.
                - Include a mixture of basic knowledge, understanding,
                  application, and simple scenario-based questions.
                - Do not ask about information that is not contained
                  in the provided lesson.
                - Avoid trick questions.
                - Avoid duplicate questions.
                - Keep questions clear and appropriate for students.
                - Do not make the correct answer obviously longer
                  than the other choices.
                - The correct answer must be exactly A, B, C, or D.

                IMPORTANT OUTPUT RULE:
                Return ONLY valid JSON.
                Do NOT use Markdown.
                Do NOT use ```json.
                Do NOT include explanations before or after the JSON.

                The JSON must follow exactly this structure:

                {
                  "questions": [
                    {
                      "questionNumber": 1,
                      "question": "Question text",
                      "optionA": "Choice A",
                      "optionB": "Choice B",
                      "optionC": "Choice C",
                      "optionD": "Choice D",
                      "correctAnswer": "A"
                    }
                  ]
                }
                """.formatted(
                lessonText,
                objectives,
                knowledge,
                examples,
                summary
        );

        String response = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        if (response == null || response.isBlank()) {
            throw new RuntimeException(
                    "AI returned an empty assessment response"
            );
        }

        try {

            return objectMapper.readValue(
                    response,
                    GeneratedAssessment.class
            );

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "AI returned invalid assessment JSON",
                    e
            );
        }
    }
}