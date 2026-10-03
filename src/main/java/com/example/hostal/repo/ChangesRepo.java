package com.example.hostal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hostal.entities.Changes;

public interface ChangesRepo extends JpaRepository<Changes, Integer> {
    java.util.List<Changes> findByUserId(int userId);
}
