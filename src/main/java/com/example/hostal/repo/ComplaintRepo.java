package com.example.hostal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hostal.entities.Complaint;
import com.example.hostal.entities.User;

public interface ComplaintRepo extends JpaRepository<Complaint, Integer> {
    java.util.List<Complaint> findComplaintByUser(User user);

    Complaint findComplaintById(int id);
}
