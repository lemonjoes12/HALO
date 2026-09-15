package com.ptc.halo.controller;

import com.ptc.halo.dtoResponse.ProfessorDashboardResponse;
import com.ptc.halo.service.ProfessorDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professor/dashboard")
public class ProfessorDashboardController {

    private final ProfessorDashboardService
            professorDashboardService;


    public ProfessorDashboardController(
            ProfessorDashboardService professorDashboardService) {

        this.professorDashboardService =
                professorDashboardService;
    }


    @PreAuthorize("hasRole('PROFESSOR')")
    @GetMapping
    public ResponseEntity<ProfessorDashboardResponse>
    getDashboard() {

        return ResponseEntity.ok(
                professorDashboardService
                        .getDashboard()
        );
    }
}