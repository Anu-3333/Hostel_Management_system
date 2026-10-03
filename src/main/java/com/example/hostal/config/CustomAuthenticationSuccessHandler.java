package com.example.hostal.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String loginType = request.getParameter("loginType");

        boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isStudent = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        if ("admin".equals(loginType)) {
            if (isAdmin) {
                response.sendRedirect("/dashboard");
            } else {
                // User is not Admin (e.g. Student credentials on Admin page)
                request.getSession().invalidate();
                // Redirect to student login if they are a student
                if (isStudent) {
                    response.sendRedirect("/login?error=true&msg=wrong_portal");
                } else {
                    response.sendRedirect("/admin-login?error=true&msg=auth_error");
                }
            }
        } else if ("student".equals(loginType)) {
            if (isStudent) {
                response.sendRedirect("/dashboard");
            } else {
                // User is not Student (e.g. Admin credentials on Student page)
                request.getSession().invalidate();
                // Redirect to admin login if they are an admin
                if (isAdmin) {
                    response.sendRedirect("/admin-login?error=true&msg=wrong_portal");
                } else {
                    response.sendRedirect("/login?error=true&msg=auth_error");
                }
            }
        } else {
            // Fallback
            if (isAdmin) {
                response.sendRedirect("/dashboard_admin");
            } else {
                response.sendRedirect("/dashboard");
            }
        }
    }
}
