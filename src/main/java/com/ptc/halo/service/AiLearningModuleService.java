package com.ptc.halo.service;

import com.ptc.halo.entity.AiLearningModuleEntity;
import com.ptc.halo.repository.AiLearningModuleRepository;
import org.springframework.stereotype.Service;

@Service
public class AiLearningModuleService {

    private final AiLearningModuleRepository aiLearningModuleRepository;

    public AiLearningModuleService(
            AiLearningModuleRepository aiLearningModuleRepository) {
        this.aiLearningModuleRepository = aiLearningModuleRepository;
    }

    public AiLearningModuleEntity createModule(
            AiLearningModuleEntity module) {

        return aiLearningModuleRepository.save(module);
    }

    public AiLearningModuleEntity getModule(Long id) {

        return aiLearningModuleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "AI Learning Module not found"
                        )
                );
    }
    public AiLearningModuleEntity getModuleByWeekId(Long weekId) {

        return aiLearningModuleRepository
                .findByWeekId(weekId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "AI Learning Module not found for this week"
                        )
                );
    }
}