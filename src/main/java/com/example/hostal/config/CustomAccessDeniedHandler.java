package com.example.hostal.config;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            // Check roles and redirect appropriately
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                // Admin trying to access unauthorized page (Student page)
                response.sendRedirect(request.getContextPath() + "/dashboard_admin");
            } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
                // Student trying to access unauthorized page (Admin page)
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/index");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }
}
