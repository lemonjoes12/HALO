package com.ptc.halo.service;

import com.ptc.halo.dtoResponse.StudentModuleProgressResponse;
import com.ptc.halo.entity.StudentModuleProgressEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.StudentModuleProgressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentProgressService {

    private final StudentModuleProgressRepository
            studentModuleProgressRepository;

    public StudentProgressService(
            StudentModuleProgressRepository
                    studentModuleProgressRepository) {

        this.studentModuleProgressRepository =
                studentModuleProgressRepository;
    }

    public List<StudentModuleProgressResponse> getProgress(
            UserEntity student) {

        List<StudentModuleProgressEntity> progressList =
                studentModuleProgressRepository
                        .findByStudentIdOrderByCompletedAtDesc(
                                student.getId()
                        );

        return progressList.stream()
                .map(progress -> {

                    StudentModuleProgressResponse response =
                            new StudentModuleProgressResponse();

                    response.setModuleId(
                            progress.getModule().getId()
                    );

                    response.setWeekId(
                            progress.getModule()
                                    .getWeek()
                                    .getId()
                    );

                    response.setCompleted(
                            progress.getCompleted()
                    );

                    response.setCompletedAt(
                            progress.getCompletedAt()
                    );

                    return response;
                })
                .collect(Collectors.toList());
    }
}