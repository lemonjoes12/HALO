package com.ptc.halo.repository;

import com.ptc.halo.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {

    Optional<SubjectEntity> findBySubjectCode(String subjectCode);

    Optional<SubjectEntity> findBySubjectName(String subjectName);

}