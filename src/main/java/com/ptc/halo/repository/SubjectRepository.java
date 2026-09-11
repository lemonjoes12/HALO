package com.ptc.halo.repository;

import com.ptc.halo.entity.SubjectEntity;
import com.ptc.halo.enums.YearLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository
        extends JpaRepository<SubjectEntity, Long> {

    Optional<SubjectEntity> findBySubjectCode(
            String subjectCode
    );

    Optional<SubjectEntity> findBySubjectName(
            String subjectName
    );

    List<SubjectEntity> findByYearLevel(
            YearLevel yearLevel
    );
}