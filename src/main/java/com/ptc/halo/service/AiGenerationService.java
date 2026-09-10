package com.ptc.halo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.halo.dtoResponse.AiGenerationResponse;
import com.ptc.halo.entity.AiLearningFileEntity;
import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.enums.AiGenerationStatus;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.repository.AiLearningModuleRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiGenerationService {

    private final ChatClient chatClient;
    private final AiLearningModuleRepository aiLearningModuleRepository;
    private final ObjectMapper objectMapper;

    public AiGenerationService(
            ChatClient.Builder chatClientBuilder,
            AiLearningModuleRepository aiLearningModuleRepository,
            ObjectMapper objectMapper) {

        this.chatClient = chatClientBuilder.build();
        this.aiLearningModuleRepository = aiLearningModuleRepository;
        this.objectMapper = objectMapper;
    }

    public AiLearningModuleEntity generateLesson(Long moduleId) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository
                        .findWithFilesById(moduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI Learning Module not found"
                                )
                        );


        module.setStatus(null);

        module.setGeneratedObjectives(null);
        module.setGeneratedKnowledge(null);
        module.setGeneratedExamples(null);
        module.setGeneratedSummary(null);

        module.setAiGenerationStatus(
                AiGenerationStatus.PENDING
        );

        aiLearningModuleRepository.save(module);

        String prompt = """
                You are HALO, an AI learning assistant for Hospitality students.

                The professor provided:

                LESSON:
                %s

                AI NOTES:
                %s

                The professor also uploaded one or more learning materials.

                Your task is to analyze ALL uploaded learning materials
                and determine whether they are relevant to the professor's lesson.

                If the uploaded materials are NOT relevant to the lesson,
                do NOT create a lesson based on unrelated information.

                Return ONLY valid JSON using this exact structure:

                {
                  "valid": false,
                  "reason": "Explain clearly why the uploaded materials are not relevant.",
                  "objectives": "",
                  "knowledge": "",
                  "examples": "",
                  "summary": ""
                }

                If the uploaded materials ARE relevant, generate a
                beginner-friendly Hospitality lesson based primarily
                on the uploaded materials, while using the professor's
                lesson and AI notes as guidance.

                Return ONLY valid JSON using this exact structure:

                {
                  "valid": true,
                  "reason": "",
                  "objectives": "Learning objectives for the lesson.",
                  "knowledge": "Important concepts and knowledge from the uploaded materials.",
                  "examples": "Practical examples and scenarios related to the lesson.",
                  "summary": "A clear and concise summary of the lesson."
                }

                IMPORTANT:
                - Return ONLY valid JSON.
                - Do not use Markdown code fences.
                - Do not add explanations outside the JSON.
                - Analyze ALL uploaded files.
                - Images may contain important text, diagrams, tables, or visual information.
                - PDFs may contain text, images, diagrams, and tables.
                - Do not invent information unrelated to the uploaded materials.
                - Keep the content appropriate for Hospitality students.
                - Keep the language beginner-friendly.
                - Keep the four generated sections separate.
                """.formatted(
                module.getLessonText(),
                module.getAiNotes()
        );

        List<Media> mediaList = new ArrayList<>();

        // Load all uploaded learning materials
        if (module.getFiles() != null &&
                !module.getFiles().isEmpty()) {

            for (AiLearningFileEntity fileEntity :
                    module.getFiles()) {

                File file =
                        new File(fileEntity.getFilePath());

                if (!file.exists()) {

                    module.setAiGenerationStatus(
                            AiGenerationStatus.DECLINED
                    );

                    aiLearningModuleRepository.save(module);

                    throw new RuntimeException(
                            "Uploaded file not found: "
                                    + fileEntity.getFilePath()
                    );
                }

                MediaType mediaType;

                try {

                    mediaType =
                            MediaType.parseMediaType(
                                    fileEntity.getFileType()
                            );

                } catch (Exception e) {

                    module.setAiGenerationStatus(
                            AiGenerationStatus.DECLINED
                    );

                    aiLearningModuleRepository.save(module);

                    throw new RuntimeException(
                            "Invalid file type: "
                                    + fileEntity.getFileType(),
                            e
                    );
                }

                Media media =
                        new Media(
                                mediaType,
                                new FileSystemResource(file)
                        );

                mediaList.add(media);
            }
        }

        String generatedContent;

        try {

            if (!mediaList.isEmpty()) {

                generatedContent =
                        chatClient.prompt()
                                .user(userSpec -> {

                                    userSpec.text(prompt);

                                    for (Media media :
                                            mediaList) {

                                        userSpec.media(media);
                                    }

                                })
                                .call()
                                .content();

            } else {

                generatedContent =
                        chatClient.prompt()
                                .user(prompt)
                                .call()
                                .content();
            }

        } catch (Exception e) {

            module.setAiGenerationStatus(
                    AiGenerationStatus.DECLINED
            );

            aiLearningModuleRepository.save(module);

            throw new RuntimeException(
                    "Failed to generate lesson using Gemini",
                    e
            );
        }

        try {

            AiGenerationResponse aiResponse =
                    objectMapper.readValue(
                            generatedContent,
                            AiGenerationResponse.class
                    );


            if (!aiResponse.isValid()) {

                module.setGeneratedObjectives(null);
                module.setGeneratedKnowledge(null);
                module.setGeneratedExamples(null);
                module.setGeneratedSummary(null);

                module.setStatus(null);

                module.setAiGenerationStatus(
                        AiGenerationStatus.DECLINED
                );

            }

            else {

                module.setGeneratedObjectives(
                        aiResponse.getObjectives()
                );

                module.setGeneratedKnowledge(
                        aiResponse.getKnowledge()
                );

                module.setGeneratedExamples(
                        aiResponse.getExamples()
                );

                module.setGeneratedSummary(
                        aiResponse.getSummary()
                );

                module.setAiGenerationStatus(
                        AiGenerationStatus.COMPLETED
                );
            }

        } catch (Exception e) {

            module.setGeneratedObjectives(null);
            module.setGeneratedKnowledge(null);
            module.setGeneratedExamples(null);
            module.setGeneratedSummary(null);

            module.setStatus(null);

            module.setAiGenerationStatus(
                    AiGenerationStatus.DECLINED
            );

            aiLearningModuleRepository.save(module);

            throw new RuntimeException(
                    "Failed to parse Gemini response",
                    e
            );
        }

        return aiLearningModuleRepository.save(module);
    }
    public AiLearningModuleEntity approveLesson(Long moduleId) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository
                        .findWithFilesById(moduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI Learning Module not found"
                                )
                        );

        if (module.getAiGenerationStatus()
                != AiGenerationStatus.COMPLETED) {

            throw new RuntimeException(
                    "Only completed AI generations can be approved"
            );
        }

        module.setStatus(LessonStatus.APPROVED);

        return aiLearningModuleRepository.save(module);
    }
    public AiLearningModuleEntity declineLesson(Long moduleId) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository
                        .findWithFilesById(moduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI Learning Module not found"
                                )
                        );

        if (module.getAiGenerationStatus()
                != AiGenerationStatus.COMPLETED) {

            throw new RuntimeException(
                    "Only completed AI generations can be declined"
            );
        }

        module.setStatus(LessonStatus.DECLINED);

        return aiLearningModuleRepository.save(module);
    }
}