package com.example.hostal.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hostal.entities.Payment;
import com.example.hostal.entities.User;



public interface PaymentRepo extends JpaRepository<Payment,Integer>{

    // List<Payment> findByUser(int i);
    List<Payment> findByUser(User user);
    
}
