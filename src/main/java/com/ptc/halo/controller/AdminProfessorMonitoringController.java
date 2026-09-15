package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.AdminProfessorMonitoringResponse;
import com.ptc.halo.service.AdminProfessorMonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/professors")
public class AdminProfessorMonitoringController {

    private final AdminProfessorMonitoringService
            adminProfessorMonitoringService;


    public AdminProfessorMonitoringController(
            AdminProfessorMonitoringService adminProfessorMonitoringService) {

        this.adminProfessorMonitoringService =
                adminProfessorMonitoringService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/monitoring")
    public ResponseEntity<List<AdminProfessorMonitoringResponse>>
    getProfessorMonitoring() {

        return ResponseEntity.ok(
                adminProfessorMonitoringService
                        .getProfessorMonitoring()
        );
    }
}