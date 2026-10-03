package com.example.hostal.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.hostal.entities.User;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private User user;
    private com.example.hostal.entities.Admin admin;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public CustomUserDetails(com.example.hostal.entities.Admin admin) {
        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.admin != null) {
            return Collections.singletonList(new SimpleGrantedAuthority(admin.getRole()));
        }
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRoles()));
    }

    @Override
    public String getPassword() {
        if (this.admin != null) {
            return admin.getPassword();
        }
        return user.getUserPassword();
    }

    @Override
    public String getUsername() {
        if (this.admin != null) {
            return admin.getEmail();
        }
        return user.getUserEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
