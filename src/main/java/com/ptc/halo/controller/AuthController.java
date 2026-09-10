package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.LoginRequest;
import com.ptc.halo.dtoRequest.StudentRequest;
import com.ptc.halo.dtoResponse.LoginResponse;
import com.ptc.halo.dtoResponse.StudentResponse;
import com.ptc.halo.service.AuthService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register/student")
    public ResponseEntity<StudentResponse> registerStudent(@RequestBody StudentRequest studentRequest){
    StudentResponse response = authService.registerStudent(studentRequest);

    return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest){
    LoginResponse response = authService.login(loginRequest);

    return ResponseEntity.ok(response);
    }
}
