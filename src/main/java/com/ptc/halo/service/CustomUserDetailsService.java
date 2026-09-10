package com.ptc.halo.service;

import com.ptc.halo.entity.UserEntity;
import com.ptc.halo.repository.UserRepository;
import com.ptc.halo.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->
                new  UsernameNotFoundException("User not Found"));
        return new CustomUserDetails(user);
    }

}
