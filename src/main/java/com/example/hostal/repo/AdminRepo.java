package com.example.hostal.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hostal.entities.Admin;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Integer> {

    Admin findByEmail(String email);

}
