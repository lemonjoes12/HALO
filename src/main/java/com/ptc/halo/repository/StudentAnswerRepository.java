package com.ptc.halo.repository;

import com.ptc.halo.entity.StudentAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentAnswerRepository
        extends JpaRepository<StudentAnswerEntity, Long> {

    List<StudentAnswerEntity>
    findByAttemptIdOrderByQuestionQuestionNumberAsc(
            Long attemptId
    );
}