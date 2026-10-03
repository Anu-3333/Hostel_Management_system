package com.example.hostal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.hostal.entities.User;
import com.example.hostal.repo.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private com.example.hostal.repo.AdminRepo adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByUserEmail(email);
        if (user != null) {
            return new CustomUserDetails(user);
        }

        com.example.hostal.entities.Admin admin = adminRepo.findByEmail(email);
        if (admin != null) {
            return new CustomUserDetails(admin);
        }

        throw new UsernameNotFoundException("User not found");
    }

}
