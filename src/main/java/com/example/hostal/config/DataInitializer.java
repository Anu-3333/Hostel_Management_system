package com.example.hostal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.hostal.entities.Admin;
import com.example.hostal.repo.AdminRepo;

@Configuration
public class DataInitializer {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (adminRepo.findByEmail("admin@hostel.com") == null) {
                Admin admin = new Admin();
                admin.setName("Default Admin");
                admin.setEmail("admin@hostel.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setPhone("1234567890");
                admin.setRole("ROLE_ADMIN");
                adminRepo.save(admin);
                System.out.println("************************************************************");
                System.out.println("DEFAULT ADMIN CREATED: admin@hostel.com / admin123");
                System.out.println("************************************************************");
            }
        };
    }
}
