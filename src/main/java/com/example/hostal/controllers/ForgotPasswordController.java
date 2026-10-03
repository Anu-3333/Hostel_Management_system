package com.example.hostal.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hostal.entities.User;
import com.example.hostal.repo.UserRepo;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        User user = userRepo.findUserByUserEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "We could not find an account with that email.");
            return "redirect:/login";
        }

        // Generate a random token (for demonstration, we act as if we verify it, but we'll just pass the email for simplicity in this mock)
        // In a real app, save this token to the database with an expiry.
        String token = UUID.randomUUID().toString();
        
        // Mock sending email
        String resetLink = "http://localhost:8080/reset-password?email=" + email + "&token=" + token;
        System.out.println("=================================================");
        System.out.println("PASSWORD RESET LINK FOR " + email + ":");
        System.out.println(resetLink);
        System.out.println("=================================================");

        redirectAttributes.addFlashAttribute("success", "A password reset link has been printed to the server console.");
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("email") String email, @RequestParam("token") String token, Model model) {
        // In a real app, validate the token here.
        model.addAttribute("email", email);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("email") String email, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        User user = userRepo.findUserByUserEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid request.");
            return "redirect:/login";
        }

        user.setUserPassword(passwordEncoder.encode(password));
        userRepo.save(user);

        redirectAttributes.addFlashAttribute("success", "You have successfully reset your password. Please login.");
        return "redirect:/login";
    }
}
