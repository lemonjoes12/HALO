package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.*;
import com.ptc.halo.dtoResponse.*;
import com.ptc.halo.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;


    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-professor")
    public ResponseEntity<ProfessorsResponse> createProfessor(
            @RequestBody ProfessorRequest professorRequest
    ){
        ProfessorsResponse response =
                adminService.createProfessor(professorRequest);

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
            @RequestBody ProfessorUpdateRequest request
    ){

        return ResponseEntity.ok(
                adminService.updateProfessor(id, request)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/professors/{id}/status")
    public ResponseEntity<ProfessorResponse> changeProfessorStatus(
            @PathVariable Long id
    ){

        return ResponseEntity.ok(
                adminService.changeProfessorStatus(id)
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
            @PathVariable Long id
    ){

        return ResponseEntity.ok(
                adminService.changeStudentStatus(id)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/students/{id}")
    public ResponseEntity<StudentListResponse> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentUpdateRequest request
    ){

        return ResponseEntity.ok(
                adminService.updateStudent(id, request)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/subjects")
    public ResponseEntity<SubjectResponse> createSubject(
            @RequestBody SubjectRequest request
    ){

        return ResponseEntity.ok(
                adminService.createSubject(request)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectResponse>> viewAllSubjects(){

        return ResponseEntity.ok(
                adminService.viewAllSubjects()
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/subjects/{id}")
    public ResponseEntity<SubjectResponse> viewSubjectById(
            @PathVariable Long id
    ){

        return ResponseEntity.ok(
                adminService.viewSubjectById(id)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/subjects/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable Long id,
            @RequestBody SubjectUpdateRequest request
    ){

        return ResponseEntity.ok(
                adminService.updateSubject(id, request)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<String> deleteSubject(
            @PathVariable Long id
    ){

        adminService.deleteSubject(id);

        return ResponseEntity.ok(
                "Subject deleted successfully"
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/subjects/{subjectId}/weeks")
    public ResponseEntity<WeekResponse> createWeek(
            @PathVariable Long subjectId,
            @RequestBody WeekRequest request
    ){

        return ResponseEntity.ok(
                adminService.createWeek(subjectId, request)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/subjects/{subjectId}/weeks")
    public ResponseEntity<List<WeekResponse>> viewAllWeeks(
            @PathVariable Long subjectId
    ){

        return ResponseEntity.ok(
                adminService.viewAllWeeks(subjectId)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/weeks/{id}")
    public ResponseEntity<WeekResponse> viewWeekById(
            @PathVariable Long id
    ){

        return ResponseEntity.ok(
                adminService.viewWeekById(id)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/weeks/{id}")
    public ResponseEntity<WeekResponse> updateWeek(
            @PathVariable Long id,
            @RequestBody WeekUpdateRequest request
    ){

        return ResponseEntity.ok(
                adminService.updateWeek(id, request)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/weeks/{id}")
    public ResponseEntity<String> deleteWeek(
            @PathVariable Long id
    ){

        adminService.deleteWeek(id);

        return ResponseEntity.ok(
                "Week deleted successfully"
        );
    }

}