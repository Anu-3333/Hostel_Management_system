package com.example.hostal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hostal.entities.Checkout;
import com.example.hostal.entities.User;

public interface CheckoutRepo extends JpaRepository<Checkout,Integer>{
    Checkout findByUser(User user);
}
