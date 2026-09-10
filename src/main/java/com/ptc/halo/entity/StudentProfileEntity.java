package com.ptc.halo.entity;

import com.ptc.halo.enums.YearLevel;
import jakarta.persistence.*;

@Entity
public class StudentProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String studentId;
    private String section;

    @Enumerated(EnumType.STRING)
    private YearLevel yearLevel;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public StudentProfileEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public YearLevel getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(YearLevel yearLevel) {
        this.yearLevel = yearLevel;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
