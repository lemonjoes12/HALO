package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.AdminRequest;
import com.ptc.halo.dtoRequest.UpdateAdminRequest;
import com.ptc.halo.dtoResponse.*;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.service.ActivityLogService;
import com.ptc.halo.service.AuthService;
import com.ptc.halo.service.SuperAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {
    private final SuperAdminService superAdminService;
    private final ActivityLogService activityLogService;
    private final UserRepository userRepository;

    public SuperAdminController(SuperAdminService superAdminService, ActivityLogService activityLogService, UserRepository userRepository) {
        this.superAdminService = superAdminService;
        this.activityLogService = activityLogService;
        this.userRepository = userRepository;


    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/create-admin")
    public ResponseEntity<AdminResponse> createAdmin(
            @RequestBody AdminRequest adminRequest,
            Authentication authentication) {

        UserEntity superAdmin =
                getCurrentSuperAdmin(authentication);

        return ResponseEntity.ok(
                superAdminService.createAdmin(
                        adminRequest,
                        superAdmin
                )
        );
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/admins")
    public ResponseEntity<List<AdminListResponse>> viewAllAdmins() {

        return ResponseEntity.ok(
                superAdminService.viewAllAdmins()
        );

    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<AdminListResponse> viewAdminById(
            @PathVariable Long id){

        return ResponseEntity.ok(
                superAdminService.viewAdminById(id)
        );
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<AdminListResponse> updateAdmin(
            @PathVariable Long id,
            @RequestBody UpdateAdminRequest request,
            Authentication authentication) {

        UserEntity superAdmin =
                getCurrentSuperAdmin(authentication);

        return ResponseEntity.ok(
                superAdminService.updateAdmin(
                        id,
                        request,
                        superAdmin
                )
        );
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<AdminListResponse> changeAdminStatus(
            @PathVariable Long id,
            Authentication authentication) {

        UserEntity superAdmin =
                getCurrentSuperAdmin(authentication);

        return ResponseEntity.ok(
                superAdminService.changeAdminStatus(
                        id,
                        superAdmin
                )
        );
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<SuperAdminDashboardResponse> getDashboard(){

        return ResponseEntity.ok(
                superAdminService.getDashboard()
        );
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/reports/users")
    public ResponseEntity<UserReportResponse> getUserReport(){

        return ResponseEntity.ok(
                superAdminService.getUserReport()
        );
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/activity-logs")
    public ResponseEntity<List<ActivityLogResponse>> getActivityLogs(){

        return ResponseEntity.ok(
                activityLogService.getAllLogs()
        );
    }
    private UserEntity getCurrentSuperAdmin(
            Authentication authentication) {

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Super Admin not found"
                        )
                );
    }

}
