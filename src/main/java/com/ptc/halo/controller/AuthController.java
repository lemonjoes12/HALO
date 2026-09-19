package com.ptc.halo.controller;

import com.ptc.halo.dtoRequest.*;
import com.ptc.halo.dtoResponse.LoginResponse;
import com.ptc.halo.dtoResponse.StudentResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.security.JwtService;
import com.ptc.halo.service.AuthService;
import com.ptc.halo.service.PasswordResetService;
import com.ptc.halo.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;
    private final UserSessionService userSessionService;
    private final JwtService jwtService;


    public AuthController(AuthService authService, PasswordResetService passwordResetService, UserRepository userRepository, UserSessionService userSessionService, JwtService jwtService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
        this.userRepository = userRepository;
        this.userSessionService = userSessionService;
        this.jwtService = jwtService;
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
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request) {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            throw new RuntimeException(
                    "Authorization token is required"
            );
        }

        String token =
                authHeader.substring(7);

        String sessionId =
                jwtService.extractSessionId(token);

        userSessionService.logout(sessionId);

        return ResponseEntity.ok(
                "Logged out successfully"
        );
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        passwordResetService.sendOtp(
                request.getEmail()
        );

        return ResponseEntity.ok(
                "Password reset OTP sent successfully"
        );
    }
    @PostMapping("/forgot-password/resend")
    public ResponseEntity<String> resendPasswordOtp(
            @RequestBody ResendPasswordOtpRequest request) {

        passwordResetService.sendOtp(
                request.getEmail()
        );

        return ResponseEntity.ok(
                "New password reset OTP sent successfully"
        );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                "Password reset successfully"
        );
    }
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        UserEntity user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        passwordResetService.changePassword(
                user,
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }

}
