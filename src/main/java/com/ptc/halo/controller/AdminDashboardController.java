package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.AdminDashboardResponse;
import com.ptc.halo.dtoResponse.AdminRecentActivityResponse;
import com.ptc.halo.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService
            adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService) {

        this.adminDashboardService =
                adminDashboardService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<AdminDashboardResponse>
    getDashboard() {

        return ResponseEntity.ok(
                adminDashboardService
                        .getDashboard()
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/recent-activity")
    public ResponseEntity<List<AdminRecentActivityResponse>>
    getRecentActivity() {

        return ResponseEntity.ok(
                adminDashboardService
                        .getRecentActivity()
        );
    }
}