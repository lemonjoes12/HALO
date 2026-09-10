package com.ptc.halo.service;

import com.ptc.halo.dtoRequest.LoginRequest;
import com.ptc.halo.dtoRequest.StudentRequest;
import com.ptc.halo.dtoResponse.LoginResponse;
import com.ptc.halo.dtoResponse.StudentResponse;
import com.ptc.halo.entity.StudentProfileEntity;
import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.Role;
import com.ptc.halo.enums.Status;
import com.ptc.halo.repository.StudentProfileRepository;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthService(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {

        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

    }


    public StudentResponse registerStudent(StudentRequest studentRequest){

        if(!studentRequest.getEmail()
                .endsWith("@paterostechnologicalcollege.edu.ph")){

            throw new RuntimeException("Invalid School Email");

        }


        if(userRepository.existsByEmail(studentRequest.getEmail())){
            throw new RuntimeException("Email already exists");
        }


        UserEntity user = new UserEntity();

        user.setName(studentRequest.getName());
        user.setEmail(studentRequest.getEmail());
        user.setPassword(
                passwordEncoder.encode(studentRequest.getPassword())
        );

        user.setRole(Role.STUDENT);
        user.setStatus(Status.ACTIVE);


        UserEntity savedUser =
                userRepository.save(user);



        StudentProfileEntity student =
                new StudentProfileEntity();

        student.setStudentId(studentRequest.getStudentId());
        student.setSection(studentRequest.getSection());
        student.setYearLevel(studentRequest.getYearLevel());
        student.setUser(savedUser);


        StudentProfileEntity savedStudent =
                studentProfileRepository.save(student);



        StudentResponse response =
                new StudentResponse();


        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setStudentId(savedStudent.getStudentId());
        response.setSection(savedStudent.getSection());
        response.setYearLevel(savedStudent.getYearLevel());


        return response;

    }



    public LoginResponse login(LoginRequest loginRequest){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );


        UserEntity user =
                userRepository.findByEmail(loginRequest.getEmail())
                        .orElseThrow();



        String token =
                jwtService.generateToken(user.getEmail());

        LoginResponse response = new LoginResponse();


        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setToken(token);


        return response;

    }

}