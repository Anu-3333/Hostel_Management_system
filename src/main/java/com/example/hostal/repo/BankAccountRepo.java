package com.example.hostal.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hostal.entities.BankAccount;
import com.example.hostal.entities.User;



public interface BankAccountRepo extends JpaRepository<BankAccount, Integer>{
    List<BankAccount> findByUser(User user);
    BankAccount findByCardNumber(String cardNumber);
}
