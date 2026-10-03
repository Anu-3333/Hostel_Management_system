package com.example.hostal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hostal.entities.User;
import com.example.hostal.repo.UserRepo;

import java.util.Collections;

@Controller
public class UserController {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.example.hostal.repo.AdminRepo adminRepo;

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerStudent(@RequestBody RegistrationRequest request) {

        if (userRepository.findUserByUserEmail(request.getUserEmail()) != null) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "User with this email already exists."));
        }

        User user = new User();
        String role = "ROLE_USER";
        if (request.getIsAdminRole() != null && request.getIsAdminRole()) {
            role = "ROLE_ADMIN";
        }
        user.setRoles(role);
        user.setUserEmail(request.getUserEmail());
        user.setUserName(request.getUserName());
        user.setUserPhone(request.getUserPhone());
        user.setAddress(request.getAddress());
        user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(Collections.singletonMap("success", "Registration successful"));
    }

    static class RegistrationRequest {
        private String userEmail;
        private String userName;
        private String userPhone;
        private String userPassword;
        private String address;
        private Boolean isAdminRole;

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPhone() {
            return userPhone;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }

        public String getUserPassword() {
            return userPassword;
        }

        public void setUserPassword(String userPassword) {
            this.userPassword = userPassword;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Boolean getIsAdminRole() {
            return isAdminRole;
        }

        public void setIsAdminRole(Boolean adminRole) {
            isAdminRole = adminRole;
        }
    }

    static class AdminRegistrationRequest {
        private String name;
        private String email;
        private String password;
        private String phone;
        private Integer adminId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getAdminId() {
            return adminId;
        }

        public void setAdminId(Integer adminId) {
            this.adminId = adminId;
        }
    }

    @PostMapping("/admin/register")
    public String registerAdmin(@ModelAttribute AdminRegistrationRequest request,
            RedirectAttributes redirectAttributes) {

        System.out.println("DEBUG: Registering Admin: " + request.getEmail() + ", ID: " + request.getAdminId());

        // Check if admin already exists in Admin table
        if (adminRepo.findByEmail(request.getEmail()) != null) {
            System.out.println("DEBUG: Email already exists");
            redirectAttributes.addFlashAttribute("error", "Admin with this email already exists.");
            return "redirect:/admin-register?error=true";
        }

        // Admin ID validation removed to allow easier registration
        // java.util.List<Integer> validAdminIds = java.util.Arrays.asList(101, 102,
        // 103, 104);
        // if (!validAdminIds.contains(request.getAdminId())) { ... }

        System.out.println("DEBUG: Validation Passed. Saving Admin.");

        com.example.hostal.entities.Admin admin = new com.example.hostal.entities.Admin();
        admin.setRole("ROLE_ADMIN");
        admin.setEmail(request.getEmail());
        admin.setName(request.getName());
        admin.setPhone(request.getPhone());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));

        adminRepo.save(admin);
        System.out.println("DEBUG: Admin Saved.");

        redirectAttributes.addFlashAttribute("success", "Admin Registration successful! Please login.");
        return "redirect:/admin-login"; // Redirect to admin login page
    }

    @GetMapping("/editstudent/{id}")
    public String editStudent(@PathVariable int id, Model model) {
        User student = userRepository.findById(id);
        model.addAttribute("student", student);
        return "edit_student"; // A separate form to edit student
    }

    @GetMapping("/deletestudent/{id}")
    public String deleteStudent(@PathVariable int id) {
        userRepository.deleteById(id);
        return "redirect:/manage_student";
    }

}
