package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.AdminStudentMonitoringResponse;
import com.ptc.halo.service.AdminStudentMonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/students")
public class AdminStudentMonitoringController {

    private final AdminStudentMonitoringService
            adminStudentMonitoringService;


    public AdminStudentMonitoringController(
            AdminStudentMonitoringService adminStudentMonitoringService) {

        this.adminStudentMonitoringService =
                adminStudentMonitoringService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/monitoring")
    public ResponseEntity<List<AdminStudentMonitoringResponse>>
    getStudentMonitoring() {

        return ResponseEntity.ok(
                adminStudentMonitoringService
                        .getStudentMonitoring()
        );
    }
}