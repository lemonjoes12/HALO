package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.ProfessorProfileResponse;
import com.ptc.halo.dtoResponse.StudentProfileResponse;
import com.ptc.halo.dtoResponse.UserProfileResponse;
import com.ptc.halo.entity.ProfessorEntity;
import com.ptc.halo.entity.StudentProfileEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.ProfessorRepository;
import com.ptc.halo.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final ProfessorRepository professorRepository;

    public ProfileService(
            StudentProfileRepository studentProfileRepository,
            ProfessorRepository professorRepository) {

        this.studentProfileRepository = studentProfileRepository;
        this.professorRepository = professorRepository;
    }


    public StudentProfileResponse getStudentProfile(
            UserEntity user) {

        StudentProfileEntity profile =
                studentProfileRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student profile not found"
                                )
                        );

        StudentProfileResponse response =
                new StudentProfileResponse();

        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());

        response.setStudentId(profile.getStudentId());
        response.setSection(profile.getSection());
        response.setYearLevel(profile.getYearLevel());

        return response;
    }


    public ProfessorProfileResponse getProfessorProfile(
            UserEntity user) {

        ProfessorEntity profile =
                professorRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Professor profile not found"
                                )
                        );

        ProfessorProfileResponse response =
                new ProfessorProfileResponse();

        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());

        response.setProfessorId(
                profile.getProfessorId()
        );

        return response;
    }


    public UserProfileResponse getUserProfile(
            UserEntity user) {

        UserProfileResponse response =
                new UserProfileResponse();

        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());

        return response;
    }
}