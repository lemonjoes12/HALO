package com.ptc.halo;

import com.ptc.halo.config.SecurityConfig;
import com.ptc.halo.controller.StudentMentorController;
import com.ptc.halo.dtoResponse.MentorConversationResponse;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.security.*;
import com.ptc.halo.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentMentorController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class StudentMentorHttpTest {
    @Autowired MockMvc mvc;
    @MockBean MentorService mentor;
    @MockBean StudentLessonAccessService access;
    @MockBean CustomUserDetailsService details;
    @MockBean JwtService jwt;

    void token(String role) {
        when(jwt.extractUsername("test-token")).thenReturn("student@example.test");
        when(details.loadUserByUsername("student@example.test")).thenReturn(
                User.withUsername("student@example.test").password("unused").roles(role).build());
    }
    @Test void bearerStudentPostReturns200WithModuleId() throws Exception {
        token("STUDENT"); var student = new UserEntity();
        when(access.requireStudent(any())).thenReturn(student);
        var response = new MentorConversationResponse(); response.setSessionId(21L); response.setModuleId(15L);
        when(mentor.openSession(15L, student)).thenReturn(response);
        mvc.perform(post("/api/student/mentor/open/15").header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.moduleId").value(15));
    }
    @Test void missingAuthenticationReturns401() throws Exception {
        mvc.perform(post("/api/student/mentor/open/15")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
    }
    @Test void invalidTokenReturns401() throws Exception {
        when(jwt.extractUsername("bad")).thenThrow(new io.jsonwebtoken.MalformedJwtException("bad"));
        mvc.perform(post("/api/student/mentor/open/15").header("Authorization", "Bearer bad"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("INVALID_OR_EXPIRED_TOKEN"));
    }
    @Test void professorCannotOpenStudentSession() throws Exception {
        token("PROFESSOR"); mvc.perform(post("/api/student/mentor/open/15").header("Authorization", "Bearer test-token"))
                .andExpect(status().isForbidden()); verifyNoInteractions(mentor);
    }
    @Test void notEnrolledReturnsExplicit403() throws Exception {
        token("STUDENT"); when(mentor.openSession(eq(15L), any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "STUDENT_NOT_ENROLLED"));
        mvc.perform(post("/api/student/mentor/open/15").header("Authorization", "Bearer test-token"))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("STUDENT_NOT_ENROLLED"));
    }
    @Test void getIs405NotMisleading403() throws Exception {
        token("STUDENT"); mvc.perform(get("/api/student/mentor/open/15").header("Authorization", "Bearer test-token"))
                .andExpect(status().isMethodNotAllowed()).andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }
    @Test void unexpectedFailureIs500Not403() throws Exception {
        token("STUDENT"); when(mentor.openSession(eq(15L), any())).thenThrow(new RuntimeException("internal detail"));
        mvc.perform(post("/api/student/mentor/open/15").header("Authorization", "Bearer test-token"))
                .andExpect(status().isInternalServerError()).andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }
}
