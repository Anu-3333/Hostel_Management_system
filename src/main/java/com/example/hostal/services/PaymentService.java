package com.example.hostal.services;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hostal.entities.Payment;
import com.example.hostal.entities.User;
import com.example.hostal.repo.PaymentRepo;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepo paymentRepository;

    public List<Payment> findByUser(User user) {
        return paymentRepository.findByUser(user);
    }

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByUser(user);
    }
   

}
