package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.*;
import com.ptc.halo.dtoResponse.*;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.ActivityType;
import com.ptc.halo.enums.Role;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.ActivityLogService;
import com.ptc.halo.service.AdminService;
import com.ptc.halo.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final ProfileService profileService;


    public AdminController(AdminService adminService, UserRepository userRepository, ActivityLogService activityLogService, ProfileService profileService) {
        this.adminService = adminService;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
        this.profileService = profileService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-professor")
    public ResponseEntity<ProfessorsResponse> createProfessor(
            @RequestBody ProfessorRequest professorRequest,
            Authentication authentication
    ) {

        UserEntity admin =
                getCurrentAdmin(authentication);

        ProfessorsResponse response =
                adminService.createProfessor(
                        professorRequest,
                        admin
                );

        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/professors")
    public ResponseEntity<List<ProfessorResponse>> viewAllProfessors(){

        return ResponseEntity.ok(
                adminService.viewAllProfessors()
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/professors/{id}")
    public ResponseEntity<ProfessorResponse> viewProfessorById(
            @PathVariable Long id
    ){

        return ResponseEntity.ok(
                adminService.viewProfessorById(id)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/professors/{id}")
    public ResponseEntity<ProfessorResponse> updateProfessor(
            @PathVariable Long id,
            @RequestBody ProfessorUpdateRequest request,
            Authentication authentication
    ){

        UserEntity admin =
                getCurrentAdmin(authentication);

        return ResponseEntity.ok(
                adminService.updateProfessor(
                        id,
                        request,
                        admin
                )
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/professors/{id}/status")
    public ResponseEntity<ProfessorResponse> changeProfessorStatus(
            @PathVariable Long id,
            Authentication authentication
    ){

        UserEntity admin =
                getCurrentAdmin(authentication);

        return ResponseEntity.ok(
                adminService.changeProfessorStatus(
                        id,
                        admin
                )
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/students")
    public ResponseEntity<List<StudentListResponse>> viewAllStudents(){

        return ResponseEntity.ok(
                adminService.viewAllStudents()
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/students/{id}")
    public ResponseEntity<StudentListResponse> viewStudentById(
            @PathVariable Long id
    ){

        return ResponseEntity.ok(
                adminService.viewStudentById(id)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/students/{id}/status")
    public ResponseEntity<StudentListResponse> changeStudentStatus(
            @PathVariable Long id,
            Authentication authentication
    ){

        UserEntity admin =
                getCurrentAdmin(authentication);

        return ResponseEntity.ok(
                adminService.changeStudentStatus(
                        id,
                        admin
                )
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/students/{id}")
    public ResponseEntity<StudentListResponse> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentUpdateRequest request,
            Authentication authentication
    ){

        UserEntity admin =
                getCurrentAdmin(authentication);

        return ResponseEntity.ok(
                adminService.updateStudent(
                        id,
                        request,
                        admin
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/activity-logs")
    public ResponseEntity<List<ActivityLogResponse>>
    getActivityLogs(

            @RequestParam(required = false)
            Role role,

            @RequestParam(
                    name = "type",
                    required = false
            )
            ActivityType activityType
    ){

        return ResponseEntity.ok(
                activityLogService.getLogs(
                        role,
                        activityType
                )
        );
    }
    private UserEntity getCurrentAdmin(
            Authentication authentication) {

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Admin not found"
                        )
                );
    }
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            Authentication authentication) {

        UserEntity admin =
                userRepository
                        .findByEmail(authentication.getName())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Admin not found"
                                )
                        );

        return ResponseEntity.ok(
                profileService.getUserProfile(admin)
        );
    }

}