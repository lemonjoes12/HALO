package com.ptc.halo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.halo.entity.AiLearningFileEntity;
import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.enums.AiGenerationStatus;
import com.ptc.halo.enums.LessonStatus;
import com.ptc.halo.repository.AiLearningModuleRepository;
import com.ptc.halo.service.AiGenerationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AiGenerationServiceTest {
    private final AiLearningModuleRepository repository = mock(AiLearningModuleRepository.class);
    private final ChatClient client = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    private final AiLearningModuleEntity module = new AiLearningModuleEntity();
    private final List<AiGenerationStatus> savedStatuses = new ArrayList<>();
    private AiGenerationService service;
    private static final String VALID = "{\"valid\":true,\"objectives\":\"Learn\",\"knowledge\":\"Concept\",\"examples\":\"Example\",\"summary\":\"Summary\"}";

    @BeforeEach void setup() {
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        when(builder.build()).thenReturn(client);
        service = new AiGenerationService(builder, repository, new ObjectMapper(), mock(com.ptc.halo.component.ModuleMaterialIndex.class));
        when(repository.findWithFilesById(15L)).thenReturn(Optional.of(module));
        when(repository.save(module)).thenAnswer(invocation -> {
            savedStatuses.add(module.getAiGenerationStatus()); return module;
        });
    }
    void reply(String value) { when(client.prompt().user(anyString()).call().content()).thenReturn(value); }
    void fails() {
        var error = assertThrows(ResponseStatusException.class, () -> service.generateLesson(15L));
        assertEquals(502, error.getStatusCode().value());
        assertEquals(List.of(AiGenerationStatus.PENDING, AiGenerationStatus.FAILED), savedStatuses);
        assertEquals(LessonStatus.PENDING, module.getStatus());
        assertNull(module.getGeneratedObjectives());
        assertNull(module.getGeneratedKnowledge());
        assertNull(module.getGeneratedExamples());
        assertNull(module.getGeneratedSummary());
    }
    @Test void successfulGenerationTransitionsPendingToCompleted() {
        reply(VALID); service.generateLesson(15L);
        assertEquals(List.of(AiGenerationStatus.PENDING, AiGenerationStatus.COMPLETED), savedStatuses);
        assertEquals(LessonStatus.PENDING, module.getStatus());
        assertEquals("Summary", module.getGeneratedSummary());
    }
    @Test void apiErrorIsFailedAndClearsOldContent() {
        module.setGeneratedObjectives("Old preview");
        when(client.prompt().user(anyString()).call().content()).thenThrow(new RuntimeException("API unavailable"));
        fails();
    }
    @Test void invalidJsonIsFailed() { reply("not JSON"); fails(); }
    @Test void missingValidFlagIsFailedNotDeclined() { reply("{}"); fails(); }
    @Test void incompleteSectionsAreFailed() { reply("{\"valid\":true,\"objectives\":\"Only one section\"}"); fails(); }
    @Test void emptyReplyIsFailed() { reply(" "); fails(); }
    @Test void missingFileIsFailed() {
        AiLearningFileEntity file = new AiLearningFileEntity(); file.setFilePath(null);
        module.addFile(file); fails();
    }
    @Test void semanticRejectionIsDistinctFromTechnicalFailure() {
        reply("{\"valid\":false,\"reason\":\"No usable materials\"}");
        service.generateLesson(15L);
        assertEquals(AiGenerationStatus.DECLINED, module.getAiGenerationStatus());
        assertEquals(LessonStatus.PENDING, module.getStatus());
    }
    @Test void professorGuidanceFallbackRemainsSupported() {
        module.setLessonText("Hospitality");
        when(client.prompt().user(anyString()).call().content())
                .thenReturn("{\"valid\":false}", VALID);
        service.generateLesson(15L);
        assertEquals(AiGenerationStatus.COMPLETED, module.getAiGenerationStatus());
    }
    @Test void professorRejectionOnlyChangesLessonStatus() {
        module.setAiGenerationStatus(AiGenerationStatus.COMPLETED);
        service.declineLesson(15L);
        assertEquals(LessonStatus.DECLINED, module.getStatus());
        assertEquals(AiGenerationStatus.COMPLETED, module.getAiGenerationStatus());
    }
    @Test void publishedLessonCannotBeResetByGeneration() {
        module.setStatus(LessonStatus.APPROVED);
        var error = assertThrows(ResponseStatusException.class, () -> service.generateLesson(15L));
        assertEquals(409, error.getStatusCode().value());
        verify(repository, never()).save(any());
    }
}
