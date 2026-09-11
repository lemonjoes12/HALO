package com.ptc.halo.service;


import com.ptc.halo.dtoRequest.*;
import com.ptc.halo.dtoResponse.*;
import com.ptc.halo.entity.*;
import com.ptc.halo.enums.ActivityType;
import com.ptc.halo.enums.Role;
import com.ptc.halo.enums.Status;
import com.ptc.halo.repository.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;
    private final StudentProfileRepository studentProfileRepository;
    private final SubjectRepository subjectRepository;
    private final WeekRepository weekRepository;

    public AdminService(
            UserRepository userRepository,
            ProfessorRepository professorRepository,
            PasswordEncoder passwordEncoder,
            ActivityLogService activityLogService,
            StudentProfileRepository studentProfileRepository,
            SubjectRepository subjectRepository, WeekRepository weekRepository
    ){

        this.userRepository = userRepository;
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
        this.activityLogService = activityLogService;
        this.studentProfileRepository = studentProfileRepository;
        this.subjectRepository = subjectRepository;
        this.weekRepository = weekRepository;
    }



    public ProfessorsResponse createProfessor(ProfessorRequest request, UserEntity admin){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        
        UserEntity professor = new UserEntity();

        professor.setName(request.getName());
        professor.setEmail(request.getEmail());

        professor.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        professor.setRole(Role.PROFESSOR);
        professor.setStatus(Status.ACTIVE);


        UserEntity savedProfessor = userRepository.save(professor);


        ProfessorEntity profile = new ProfessorEntity();

        profile.setProfessorId(request.getProfessorId());

        profile.setUser(savedProfessor);

        professorRepository.save(profile);

        activityLogService.createLog(
                admin,
                ActivityType.ACCOUNT,
                "Created Professor account: "
                        + savedProfessor.getEmail()
        );

        ProfessorsResponse response =
                new ProfessorsResponse();


        response.setName(savedProfessor.getName());
        response.setEmail(savedProfessor.getEmail());
        response.setProfessorId(profile.getProfessorId());
        response.setRole(savedProfessor.getRole());


        return response;

    }
    public List<ProfessorResponse> viewAllProfessors(){

        return professorRepository.findAll()
                .stream()
                .map(professor -> {

                    ProfessorResponse response = new ProfessorResponse();

                    response.setId(professor.getUser().getId());
                    response.setName(professor.getUser().getName());
                    response.setEmail(professor.getUser().getEmail());
                    response.setProfessorId(professor.getProfessorId());
                    response.setStatus(professor.getUser().getStatus());

                    return response;

                })
                .collect(Collectors.toList());
    }
    public ProfessorResponse viewProfessorById(Long id){

        ProfessorEntity professor = professorRepository
                .findByUserId(id)
                .orElseThrow(() ->
                        new RuntimeException("Professor not found")
                );

        ProfessorResponse response = new ProfessorResponse();

        response.setId(professor.getUser().getId());
        response.setName(professor.getUser().getName());
        response.setEmail(professor.getUser().getEmail());
        response.setProfessorId(professor.getProfessorId());
        response.setStatus(professor.getUser().getStatus());

        return response;
    }
    public ProfessorResponse updateProfessor(
            Long id,
            ProfessorUpdateRequest request,
            UserEntity admin){

        ProfessorEntity professor = professorRepository
                .findByUserId(id)
                .orElseThrow(() ->
                        new RuntimeException("Professor not found")
                );

        professor.getUser().setName(request.getName());
        professor.getUser().setEmail(request.getEmail());
        professor.setProfessorId(request.getProfessorId());

        professorRepository.save(professor);

        activityLogService.createLog(
                admin,
                ActivityType.ACCOUNT,
                "Updated Professor account: "
                        + professor.getUser().getEmail()
        );

        ProfessorResponse response = new ProfessorResponse();

        response.setId(professor.getUser().getId());
        response.setName(professor.getUser().getName());
        response.setEmail(professor.getUser().getEmail());
        response.setProfessorId(professor.getProfessorId());
        response.setStatus(professor.getUser().getStatus());

        return response;
    }
    public ProfessorResponse changeProfessorStatus(Long id, UserEntity admin){

        UserEntity professorUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Professor not found")
                );


        if(professorUser.getRole() != Role.PROFESSOR){
            throw new RuntimeException("User is not a Professor");
        }

        if(professorUser.getStatus() == Status.ACTIVE){
            professorUser.setStatus(Status.INACTIVE);
        }else{
            professorUser.setStatus(Status.ACTIVE);
        }


        UserEntity updatedUser = userRepository.save(professorUser);

        activityLogService.createLog(
                admin,
                ActivityType.ACCOUNT,
                "Changed Professor status to "
                        + updatedUser.getStatus()
                        + ": "
                        + updatedUser.getEmail()
        );

        ProfessorEntity professor = professorRepository.findByUserId(id)
                .orElseThrow(() ->
                        new RuntimeException("Professor profile not found")
                );

        ProfessorResponse response = new ProfessorResponse();

        response.setId(updatedUser.getId());
        response.setName(updatedUser.getName());
        response.setEmail(updatedUser.getEmail());
        response.setProfessorId(professor.getProfessorId());
        response.setStatus(updatedUser.getStatus());


        return response;
    }
    public List<StudentListResponse> viewAllStudents(){

        return studentProfileRepository.findAll()
                .stream()
                .map(student -> {

                    StudentListResponse response =
                            new StudentListResponse();

                    response.setId(student.getUser().getId());
                    response.setName(student.getUser().getName());
                    response.setEmail(student.getUser().getEmail());
                    response.setStudentId(student.getStudentId());
                    response.setSection(student.getSection());
                    response.setYearLevel(student.getYearLevel());
                    response.setStatus(student.getUser().getStatus());

                    return response;

                })
                .collect(Collectors.toList());
    }
    public StudentListResponse viewStudentById(Long id){

        StudentProfileEntity student =
                studentProfileRepository.findByUserId(id)
                        .orElseThrow(() ->
                                new RuntimeException("Student not found")
                        );


        StudentListResponse response =
                new StudentListResponse();

        response.setId(student.getUser().getId());
        response.setName(student.getUser().getName());
        response.setEmail(student.getUser().getEmail());
        response.setStudentId(student.getStudentId());
        response.setSection(student.getSection());
        response.setYearLevel(student.getYearLevel());
        response.setStatus(student.getUser().getStatus());

        return response;
    }
    public StudentListResponse changeStudentStatus(Long id, UserEntity admin){

        UserEntity student = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found")
                );

        if(student.getRole() != Role.STUDENT){
            throw new RuntimeException("User is not a Student");
        }

        if(student.getStatus() == Status.ACTIVE){
            student.setStatus(Status.INACTIVE);
        }else{
            student.setStatus(Status.ACTIVE);
        }


        UserEntity updatedStudent = userRepository.save(student);

        activityLogService.createLog(
                admin,
                ActivityType.ACCOUNT,
                "Changed Student status to "
                        + updatedStudent.getStatus()
                        + ": "
                        + updatedStudent.getEmail()
        );

        StudentProfileEntity studentProfile =
                studentProfileRepository.findByUserId(id)
                        .orElseThrow(() ->
                                new RuntimeException("Student profile not found")
                        );

        StudentListResponse response = new StudentListResponse();

        response.setId(updatedStudent.getId());
        response.setName(updatedStudent.getName());
        response.setEmail(updatedStudent.getEmail());
        response.setStudentId(studentProfile.getStudentId());
        response.setSection(studentProfile.getSection());
        response.setYearLevel(studentProfile.getYearLevel());
        response.setStatus(updatedStudent.getStatus());

        return response;
    }
    public StudentListResponse updateStudent(
            Long id,
            StudentUpdateRequest request,
            UserEntity admin){

        StudentProfileEntity studentProfile = studentProfileRepository.findByUserId(id)
                        .orElseThrow(() -> new RuntimeException("Student not found"));


        UserEntity user = studentProfile.getUser();

        user.setName(request.getName());
        studentProfile.setStudentId(request.getStudentId());
        studentProfile.setSection(request.getSection());
        studentProfile.setYearLevel(request.getYearLevel());

        UserEntity updatedUser = userRepository.save(user);

        StudentProfileEntity updatedProfile = studentProfileRepository.save(studentProfile);

        activityLogService.createLog(
                admin,
                ActivityType.ACCOUNT,
                "Updated Student account: "
                        + updatedUser.getEmail()
        );

        StudentListResponse response = new StudentListResponse();

        response.setId(updatedUser.getId());
        response.setName(updatedUser.getName());
        response.setEmail(updatedUser.getEmail());
        response.setStudentId(updatedProfile.getStudentId());
        response.setSection(updatedProfile.getSection());
        response.setYearLevel(updatedProfile.getYearLevel());
        response.setStatus(updatedUser.getStatus());

        return response;
    }

    public SubjectResponse createSubject(
            SubjectRequest request,
            UserEntity admin){

        if(subjectRepository.findBySubjectCode(request.getSubjectCode()).isPresent()){
            throw new RuntimeException("Subject code already exists");
        }

        if(subjectRepository.findBySubjectName(request.getSubjectName()).isPresent()){
            throw new RuntimeException("Subject name already exists");
        }

        SubjectEntity subject = new SubjectEntity();

        subject.setSubjectCode(request.getSubjectCode());
        subject.setSubjectName(request.getSubjectName());
        subject.setDescription(request.getDescription());
        subject.setYearLevel(request.getYearLevel());

        SubjectEntity savedSubject = subjectRepository.save(subject);

        activityLogService.createLog(
                admin,
                ActivityType.MODULE,
                "Created subject: "
                        + savedSubject.getSubjectName()
        );

        SubjectResponse response = new SubjectResponse();

        response.setId(savedSubject.getId());
        response.setSubjectCode(savedSubject.getSubjectCode());
        response.setSubjectName(savedSubject.getSubjectName());
        response.setDescription(savedSubject.getDescription());
        response.setYearLevel(savedSubject.getYearLevel());

        return response;
    }
    public List<SubjectResponse> viewAllSubjects(){

        return subjectRepository.findAll()
                .stream()
                .map(subject -> {

                    SubjectResponse response = new SubjectResponse();


                    response.setId(subject.getId());
                    response.setSubjectCode(subject.getSubjectCode());
                    response.setSubjectName(subject.getSubjectName());
                    response.setDescription(subject.getDescription());
                    response.setYearLevel(subject.getYearLevel());

                    return response;

                })
                .collect(Collectors.toList());
    }
    public SubjectResponse viewSubjectById(Long id){

        SubjectEntity subject =
                subjectRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Subject not found")
                        );


        SubjectResponse response =
                new SubjectResponse();


        response.setId(subject.getId());
        response.setSubjectCode(subject.getSubjectCode());
        response.setSubjectName(subject.getSubjectName());
        response.setDescription(subject.getDescription());
        response.setYearLevel(subject.getYearLevel());


        return response;
    }
    public SubjectResponse updateSubject(
            Long id,
            SubjectUpdateRequest request,
            UserEntity admin){

        SubjectEntity subject =
                subjectRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Subject not found")
                        );


        subject.setSubjectCode(
                request.getSubjectCode()
        );


        subject.setSubjectName(
                request.getSubjectName()
        );


        subject.setDescription(
                request.getDescription()
        );


        subject.setYearLevel(
                request.getYearLevel()
        );


        SubjectEntity updatedSubject =
                subjectRepository.save(subject);


        activityLogService.createLog(
                admin,
                ActivityType.MODULE,
                "Updated subject: "
                        + updatedSubject.getSubjectName()
        );

        SubjectResponse response =
                new SubjectResponse();

        response.setId(updatedSubject.getId());
        response.setSubjectCode(updatedSubject.getSubjectCode());
        response.setSubjectName(updatedSubject.getSubjectName());
        response.setDescription(updatedSubject.getDescription());
        response.setYearLevel(updatedSubject.getYearLevel());


        return response;
    }
    public void deleteSubject(Long id, UserEntity admin){

        SubjectEntity subject =
                subjectRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Subject not found")
                        );

        String subjectName =
                subject.getSubjectName();

        subjectRepository.delete(subject);

        activityLogService.createLog(
                admin,
                ActivityType.MODULE,
                "Deleted subject: "
                        + subjectName
        );
    }
    public WeekResponse createWeek(
            Long subjectId,
            WeekRequest request,
            UserEntity admin){

        SubjectEntity subject =
                subjectRepository.findById(subjectId)
                        .orElseThrow(() ->
                                new RuntimeException("Subject not found")
                        );


        WeekEntity week = new WeekEntity();


        week.setWeekNumber(request.getWeekNumber());
        week.setTitle(request.getTitle());
        week.setSubject(subject);

        WeekEntity savedWeek =
                weekRepository.save(week);

        activityLogService.createLog(
                admin,
                ActivityType.MODULE,
                "Created Week "
                        + savedWeek.getWeekNumber()
                        + ": "
                        + savedWeek.getTitle()
        );

        WeekResponse response =
                new WeekResponse();

        response.setId(savedWeek.getId());
        response.setWeekNumber(savedWeek.getWeekNumber());
        response.setTitle(savedWeek.getTitle());
        response.setSubjectId(subject.getId());

        return response;
    }
    public List<WeekResponse> viewAllWeeks(Long subjectId){

        SubjectEntity subject =
                subjectRepository.findById(subjectId)
                        .orElseThrow(() ->
                                new RuntimeException("Subject not found")
                        );


        return weekRepository.findBySubject_Id(subjectId)
                .stream()
                .map(week -> {

                    WeekResponse response = new WeekResponse();

                    response.setId(week.getId());
                    response.setWeekNumber(week.getWeekNumber());
                    response.setTitle(week.getTitle());
                    response.setSubjectId(subject.getId());

                    return response;

                })
                .toList();
    }
    public WeekResponse viewWeekById(Long id){

        WeekEntity week =
                weekRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Week not found")
                        );


        WeekResponse response = new WeekResponse();

        response.setId(week.getId());
        response.setWeekNumber(week.getWeekNumber());
        response.setTitle(week.getTitle());
        response.setSubjectId(week.getSubject().getId());

        return response;
    }
    public WeekResponse updateWeek(
            Long id,
            WeekUpdateRequest request,
            UserEntity admin){

        WeekEntity week =
                weekRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Week not found"));

        week.setWeekNumber(request.getWeekNumber());
        week.setTitle(request.getTitle());

        WeekEntity updatedWeek = weekRepository.save(week);

        activityLogService.createLog(
                admin,
                ActivityType.MODULE,
                "Updated Week "
                        + updatedWeek.getWeekNumber()
                        + ": "
                        + updatedWeek.getTitle()
        );

        WeekResponse response = new WeekResponse();

        response.setId(updatedWeek.getId());
        response.setWeekNumber(updatedWeek.getWeekNumber());
        response.setTitle(updatedWeek.getTitle());
        response.setSubjectId(updatedWeek.getSubject().getId());

        return response;
    }
    public void deleteWeek(Long id, UserEntity admin){

        WeekEntity week =
                weekRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Week not found")
                        );

        var weekNumber =
                week.getWeekNumber();

        String weekTitle =
                week.getTitle();

        weekRepository.delete(week);

        activityLogService.createLog(
                admin,
                ActivityType.MODULE,
                "Deleted Week "
                        + weekNumber
                        + ": "
                        + weekTitle
        );
    }

}