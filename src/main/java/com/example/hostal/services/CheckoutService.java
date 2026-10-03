package com.example.hostal.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hostal.entities.Checkout;
import com.example.hostal.entities.User;
import com.example.hostal.repo.CheckoutRepo;
import com.example.hostal.repo.UserRepo;

@Service
public class CheckoutService {
    
    @Autowired
    private CheckoutRepo checkoutRepo;

    @Autowired
    private UserRepo userRepo;

    public List<Checkout> getpendingCheckoutList(){
        List<Checkout> list = checkoutRepo.findAll();
        List<Checkout> checkouts = new ArrayList<>();
        for(Checkout checkout: list){
            if(checkout.getStatus().equalsIgnoreCase("pending")){
                checkouts.add(checkout);
            }
        }
        return checkouts;
    }

}
