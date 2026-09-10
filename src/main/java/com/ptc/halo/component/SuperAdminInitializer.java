package com.ptc.halo.component;

import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.enums.Role;
import com.ptc.halo.enums.Status;
import com.ptc.halo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class    SuperAdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args){

        if (!userRepository.existsByRole(Role.SUPER_ADMIN)){
            UserEntity super_Admin = new UserEntity();

            super_Admin.setStatus(Status.ACTIVE);
            super_Admin.setRole(Role.SUPER_ADMIN);
            super_Admin.setPassword(passwordEncoder.encode("Admin123"));
            super_Admin.setEmail("superadmin@paterostechnologicalcollege.edu.ph");
            super_Admin.setName("HALO SUPER_ADMIN");

            userRepository.save(super_Admin);
        }

    }
}
