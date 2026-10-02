package com.ptc.halo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.halo.component.ModuleMaterialIndex;
import com.fasterxml.jackson.databind.JsonNode;
import com.ptc.halo.dtoResponse.AiGenerationResponse;
import com.ptc.halo.entity.AiLearningFileEntity;
import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.enums.AiGenerationStatus;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.repository.AiLearningModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiGenerationService {

    private static final Logger log =
            LoggerFactory.getLogger(AiGenerationService.class);

    private final ModuleMaterialIndex materialIndex;
    private final ChatClient chatClient;
    private final AiLearningModuleRepository aiLearningModuleRepository;
    private final ObjectMapper objectMapper;

    public AiGenerationService(
            ChatClient.Builder chatClientBuilder,
            AiLearningModuleRepository aiLearningModuleRepository,
            ObjectMapper objectMapper, ModuleMaterialIndex materialIndex
    ) {
        this.materialIndex = materialIndex;
        this.chatClient = chatClientBuilder.build();
        this.aiLearningModuleRepository = aiLearningModuleRepository;
        this.objectMapper = objectMapper;
    }

    // =========================================================
    // GENERATE LESSON
    // =========================================================

    public AiLearningModuleEntity generateLesson(Long moduleId) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository
                        .findWithFilesById(moduleId)
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "AI Learning Module not found")
                        );

        if (module.getStatus() == LessonStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Published lessons cannot be regenerated through the draft editor.");
        }

        // Reset previous generated content
        clearGeneratedContent(module);

        // Professor has not approved/declined the new generation yet
        module.setStatus(LessonStatus.PENDING);

        // AI is currently generating
        module.setAiGenerationStatus(
                AiGenerationStatus.PENDING
        );

        aiLearningModuleRepository.save(module);

        try {

            // =====================================================
            // LOAD FILES
            // =====================================================

            List<Media> mediaList = loadMediaFiles(module);
            if (module.getFiles() != null && !module.getFiles().isEmpty()) {
                materialIndex.getOrBuild(module);
            }

            // =====================================================
            // MAIN PROMPT
            // =====================================================

            String prompt = buildMainPrompt(module);

            log.info(
                    "Generating lesson for module {} with {} uploaded file(s)",
                    moduleId,
                    mediaList.size()
            );

            // =====================================================
            // CALL GEMINI
            // =====================================================

            String generatedContent =
                    callGemini(prompt, mediaList);

            log.info(
                    "Gemini returned a response for module {}",
                    moduleId
            );

            // =====================================================
            // PARSE GEMINI JSON
            // =====================================================

            AiGenerationResponse aiResponse =
                    parseGeminiResponse(generatedContent);

            /*
             * IMPORTANT:
             *
             * If Gemini thinks the uploaded PDF/file is unrelated,
             * but the professor provided Lesson Text or AI Notes,
             * do NOT fail the whole lesson.
             *
             * Generate from the professor's own lesson information
             * instead.
             */
            if (!aiResponse.isValid()
                    && hasProfessorGuidance(module)) {

                log.warn(
                        "Uploaded materials were considered unrelated for module {}. " +
                                "Retrying using professor lesson text/notes.",
                        moduleId
                );

                String fallbackPrompt =
                        buildFallbackPrompt(module);

                String fallbackContent =
                        callGemini(
                                fallbackPrompt,
                                new ArrayList<>()
                        );

                aiResponse =
                        parseGeminiResponse(
                                fallbackContent
                        );
            }

            // =====================================================
            // STILL INVALID
            // =====================================================

            if (!aiResponse.isValid()) {

                clearGeneratedContent(module);

                /*
                 * DECLINED here means:
                 * AI found no usable/relevant learning material.
                 *
                 * This is NOT a technical error.
                 */
                module.setAiGenerationStatus(
                        AiGenerationStatus.DECLINED
                );

                log.warn(
                        "AI declined materials for module {} because no usable lesson content was found.",
                        moduleId
                );

                return aiLearningModuleRepository.save(module);
            }

            // =====================================================
            // VALIDATE GENERATED SECTIONS
            // =====================================================

            if (!hasGeneratedContent(aiResponse)) {

                throw new RuntimeException(
                        "Gemini returned valid=true but generated lesson sections were empty"
                );
            }

            // =====================================================
            // SAVE GENERATED LESSON
            // =====================================================

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

            log.info(
                    "AI lesson generation COMPLETED for module {}",
                    moduleId
            );

            return aiLearningModuleRepository.save(module);

        } catch (Exception e) {

            // =====================================================
            // TECHNICAL FAILURE
            // =====================================================

            clearGeneratedContent(module);

            module.setStatus(LessonStatus.PENDING);

            module.setAiGenerationStatus(
                    AiGenerationStatus.FAILED
            );

            aiLearningModuleRepository.save(module);

            log.error("AI lesson generation FAILED moduleId={} exceptionType={}",
                    moduleId, e.getClass().getSimpleName());

            // This method intentionally has no encompassing transaction: the FAILED
            // repository save must persist even though the request returns an error.
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "AI generation failed. Check the backend AI configuration and uploaded files, then retry.", e);
        }
    }

    // =========================================================
    // LOAD UPLOADED FILES
    // =========================================================

    private List<Media> loadMediaFiles(
            AiLearningModuleEntity module
    ) {

        List<Media> mediaList =
                new ArrayList<>();

        if (module.getFiles() == null
                || module.getFiles().isEmpty()) {

            return mediaList;
        }

        for (AiLearningFileEntity fileEntity :
                module.getFiles()) {

            String filePath =
                    fileEntity.getFilePath();

            if (filePath == null
                    || filePath.isBlank()) {

                throw new RuntimeException(
                        "Uploaded file has no file path"
                );
            }

            File file =
                    new File(filePath);

            if (!file.exists()) {

                throw new RuntimeException(
                        "Uploaded file not found: "
                                + filePath
                );
            }

            if (!file.isFile() || !file.canRead()) {

                throw new RuntimeException(
                        "Uploaded path is not a file: "
                                + filePath
                );
            }

            if (file.length() == 0) {

                throw new RuntimeException(
                        "Uploaded file is empty: "
                                + filePath
                );
            }

            MediaType mediaType;

            try {

                mediaType =
                        MediaType.parseMediaType(
                                fileEntity.getFileType()
                        );

            } catch (Exception e) {

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

            log.info(
                    "Added learning material: {} ({})",
                    fileEntity.getOriginalFileName(),
                    fileEntity.getFileType()
            );
        }

        return mediaList;
    }

    // =========================================================
    // MAIN PROMPT
    // =========================================================

    private String buildMainPrompt(
            AiLearningModuleEntity module
    ) {

        return """
                You are HALO, an AI learning assistant for Hospitality students.

                The PROFESSOR'S LESSON TOPIC is the primary topic that
                the generated lesson must follow.

                PROFESSOR LESSON:
                %s

                PROFESSOR AI NOTES:
                %s

                YOUTUBE REFERENCE:
                %s

                The professor may also provide PDF files or images.

                Your job is to create a clear, beginner-friendly
                Hospitality lesson.

                IMPORTANT CONTENT RULES:

                1. The professor's lesson text defines the main topic.

                2. Use relevant information from the uploaded PDF,
                   image, or other learning materials.

                3. If an uploaded material contains some unrelated
                   information, IGNORE the unrelated portion instead
                   of rejecting the entire lesson.

                4. If the professor provided useful lesson text or
                   AI notes, you should still generate the lesson even
                   if an uploaded file contains limited or unrelated
                   information.

                5. Return "valid": false ONLY when there is genuinely
                   no usable educational information from:
                   - the professor lesson,
                   - professor AI notes,
                   - and uploaded learning materials.

                6. Do not invent facts that contradict the provided
                   materials.

                7. Keep the lesson appropriate for Hospitality students.

                8. Keep explanations beginner-friendly.

                9. Analyze PDF text, tables, diagrams, images, and
                   other useful information when available.

                Return ONLY valid JSON.

                Use EXACTLY this structure:

                {
                  "valid": true,
                  "reason": "",
                  "objectives": "Clear learning objectives.",
                  "knowledge": "Important concepts and lesson knowledge.",
                  "examples": "Practical examples and scenarios.",
                  "summary": "A clear summary of the lesson."
                }

                If there is absolutely no usable educational content,
                return:

                {
                  "valid": false,
                  "reason": "Explain why there is no usable learning content.",
                  "objectives": "",
                  "knowledge": "",
                  "examples": "",
                  "summary": ""
                }

                IMPORTANT:
                - Return ONLY JSON.
                - Do NOT use Markdown.
                - Do NOT use ```json code fences.
                - Do NOT write anything before the JSON.
                - Do NOT write anything after the JSON.
                - Keep objectives, knowledge, examples, and summary separate.
                """
                .formatted(
                        safeText(module.getLessonText()),
                        safeText(module.getAiNotes()),
                        safeText(module.getYoutubeLink())
                );
    }

    // =========================================================
    // FALLBACK PROMPT
    // =========================================================

    private String buildFallbackPrompt(
            AiLearningModuleEntity module
    ) {

        return """
                You are HALO, an AI learning assistant for Hospitality students.

                Generate a beginner-friendly lesson using the
                professor's lesson text and notes below.

                Ignore uploaded files for this retry.

                PROFESSOR LESSON:
                %s

                PROFESSOR AI NOTES:
                %s

                Create educational content based on this topic.

                Return ONLY valid JSON using EXACTLY this structure:

                {
                  "valid": true,
                  "reason": "",
                  "objectives": "Clear learning objectives.",
                  "knowledge": "Important lesson concepts.",
                  "examples": "Practical Hospitality examples.",
                  "summary": "A concise lesson summary."
                }

                IMPORTANT:
                - Return ONLY JSON.
                - Do NOT use Markdown.
                - Do NOT use code fences.
                - Do NOT add text outside the JSON.
                """
                .formatted(
                        safeText(module.getLessonText()),
                        safeText(module.getAiNotes())
                );
    }

    // =========================================================
    // GEMINI CALL
    // =========================================================

    private String callGemini(
            String prompt,
            List<Media> mediaList
    ) {

        String content;

        if (mediaList != null
                && !mediaList.isEmpty()) {

            content =
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

            content =
                    chatClient.prompt()
                            .user(prompt)
                            .call()
                            .content();
        }

        if (content == null
                || content.isBlank()) {

            throw new RuntimeException(
                    "Gemini returned an empty response"
            );
        }

        return content;
    }

    // =========================================================
    // PARSE GEMINI RESPONSE
    // =========================================================

    private AiGenerationResponse parseGeminiResponse(
            String generatedContent
    ) {

        try {

            String cleanedJson =
                    cleanJsonResponse(
                            generatedContent
                    );

            JsonNode json = objectMapper.readTree(cleanedJson);
            if (json == null || !json.isObject() || !json.path("valid").isBoolean()) {
                throw new IllegalArgumentException("Gemini response must contain a boolean valid field");
            }
            if (json.path("valid").booleanValue()) {
                for (String section : List.of("objectives", "knowledge", "examples", "summary")) {
                    if (!json.path(section).isTextual() || json.path(section).asText().isBlank()) {
                        throw new IllegalArgumentException("Missing or invalid generated section: " + section);
                    }
                }
            }
            return objectMapper.treeToValue(json, AiGenerationResponse.class);

        } catch (Exception e) {

            log.warn("Could not parse Gemini lesson response: {}", e.getClass().getSimpleName());

            throw new RuntimeException(
                    "Failed to parse Gemini response",
                    e
            );
        }
    }

    // =========================================================
    // CLEAN JSON
    // =========================================================

    private String cleanJsonResponse(
            String response
    ) {

        if (response == null) {
            return "";
        }

        String cleaned =
                response.trim();

        // Remove Markdown fences if Gemini accidentally includes them
        if (cleaned.startsWith("```json")) {
            cleaned =
                    cleaned.substring(7).trim();
        }

        if (cleaned.startsWith("```")) {
            cleaned =
                    cleaned.substring(3).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 3
                    ).trim();
        }

        /*
         * If Gemini accidentally adds text before/after JSON,
         * keep only the JSON object.
         */
        int firstBrace =
                cleaned.indexOf('{');

        int lastBrace =
                cleaned.lastIndexOf('}');

        if (firstBrace >= 0
                && lastBrace > firstBrace) {

            cleaned =
                    cleaned.substring(
                            firstBrace,
                            lastBrace + 1
                    );
        }

        return cleaned;
    }

    // =========================================================
    // CHECK PROFESSOR GUIDANCE
    // =========================================================

    private boolean hasProfessorGuidance(
            AiLearningModuleEntity module
    ) {

        return hasText(module.getLessonText())
                || hasText(module.getAiNotes());
    }

    // =========================================================
    // CHECK GENERATED CONTENT
    // =========================================================

    private boolean hasGeneratedContent(
            AiGenerationResponse response
    ) {

        return hasText(response.getObjectives())
                && hasText(response.getKnowledge())
                && hasText(response.getExamples())
                && hasText(response.getSummary());
    }

    // =========================================================
    // CLEAR GENERATED CONTENT
    // =========================================================

    private void clearGeneratedContent(
            AiLearningModuleEntity module
    ) {

        module.setGeneratedObjectives(null);
        module.setGeneratedKnowledge(null);
        module.setGeneratedExamples(null);
        module.setGeneratedSummary(null);
    }

    // =========================================================
    // STRING HELPERS
    // =========================================================

    private boolean hasText(
            String value
    ) {

        return value != null
                && !value.trim().isEmpty();
    }

    private String safeText(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            return "(none provided)";
        }

        return value.trim();
    }

    // =========================================================
    // APPROVE LESSON
    // =========================================================

    public AiLearningModuleEntity approveLesson(
            Long moduleId
    ) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository
                        .findWithFilesById(moduleId)
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "AI Learning Module not found")
                        );

        if (module.getAiGenerationStatus()
                != AiGenerationStatus.COMPLETED) {

            throw new RuntimeException(
                    "Only completed AI generations can be approved"
            );
        }

        module.setStatus(
                LessonStatus.APPROVED
        );

        return aiLearningModuleRepository.save(module);
    }

    // =========================================================
    // DECLINE LESSON
    // =========================================================

    public AiLearningModuleEntity declineLesson(
            Long moduleId
    ) {

        AiLearningModuleEntity module =
                aiLearningModuleRepository
                        .findWithFilesById(moduleId)
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "AI Learning Module not found")
                        );

        if (module.getAiGenerationStatus()
                != AiGenerationStatus.COMPLETED) {

            throw new RuntimeException(
                    "Only completed AI generations can be declined"
            );
        }

        /*
         * LessonStatus.DECLINED means:
         * Professor manually declined the generated lesson.
         *
         * We intentionally DO NOT change aiGenerationStatus here.
         * AI successfully generated the content, so it stays COMPLETED.
         */
        module.setStatus(
                LessonStatus.DECLINED
        );

        return aiLearningModuleRepository.save(module);
    }
}