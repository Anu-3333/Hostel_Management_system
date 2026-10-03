package com.example.hostal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.hostal.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                                                // Admin pages restricted to ADMIN role
                                                .requestMatchers("/admin/**", "/dashboard_admin", "/admin_setting",
                                                                "/manage_hostel", "/requestformadmin",
                                                                "/manage_student", "/manage_rooms", "/admin_complaint",
                                                                "/manage_bookings",
                                                                "/admin_checkout")
                                                .hasRole("ADMIN")

                                                // Student pages restricted to USER role
                                                .requestMatchers("/student_biodata", "/payment_report", "/room_report",
                                                                "/complaint", "/checkout", "/review")
                                                .hasRole("USER")

                                                // Shared or public pages
                                                .requestMatchers("/register", "/apply-room", "/gallery", "/privacy",
                                                                "/terms_of_services", "/applicationform", "/index",
                                                                "/verify-otp",
                                                                "/reset-password", "/about", "/notification",
                                                                "/addbankaccount", "/contact", "/css/**", "/js/**",
                                                                "/images/**", "/static/**", "/admin-login",
                                                                "/admin-register", "/admin/register", "/debug/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/register", "/admin/register",
                                                                "/forgot-password", "/review_form", "/submit_username",
                                                                "/approve_checkout",
                                                                "/feedbackform", "/apply-room", "/submit_complaint",
                                                                "/admin/complaintrequest", "/checkoutrequest",
                                                                "/applicationform", "/admin/approverequest", "/logout",
                                                                "/addbankaccount", "/login-user"))
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/index")
                                                .loginProcessingUrl("/login-user")
                                                .successHandler(new CustomAuthenticationSuccessHandler())
                                                .failureHandler(new CustomAuthenticationFailureHandler())
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(
                                                                new org.springframework.security.web.util.matcher.AntPathRequestMatcher(
                                                                                "/logout"))
                                                .logoutSuccessUrl("/login")
                                                .permitAll())
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .accessDeniedHandler(new CustomAccessDeniedHandler()));

                return http.build();
        }
}
