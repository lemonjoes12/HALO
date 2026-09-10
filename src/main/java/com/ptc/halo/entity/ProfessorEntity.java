package com.ptc.halo.entity;

import jakarta.persistence.*;

@Entity
public class ProfessorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String professorId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public ProfessorEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}