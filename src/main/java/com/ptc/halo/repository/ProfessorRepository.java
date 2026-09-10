package com.ptc.halo.repository;

import com.ptc.halo.entity.ProfessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<ProfessorEntity, Long> {

    Optional<ProfessorEntity> findByUserId(Long userId);

    Optional<Object> findByUser_Id(Long id);
}