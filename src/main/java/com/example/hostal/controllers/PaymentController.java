package com.example.hostal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hostal.entities.BankAccount;
import com.example.hostal.entities.Payment;
import com.example.hostal.entities.User;
import com.example.hostal.repo.BankAccountRepo;
import com.example.hostal.services.BankAccountService;
import com.example.hostal.services.DummyPaymentService;
import com.example.hostal.services.PaymentService;
import com.example.hostal.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private BankAccountRepo bankAccountRepo;

    @Autowired
    private com.example.hostal.repo.MyRoomRepo myRoomRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DummyPaymentService dummyPaymentService;

    @GetMapping("/payment")
    public String showPaymentPage(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Payment> payments = paymentService.findByUser(user);
        model.addAttribute("payments", payments);
        model.addAttribute("bankAccounts", bankAccountService.getBankAccountsByUser(user));

        // Add User and Room details
        model.addAttribute("user", user);
        com.example.hostal.entities.MyRoom myRoom = myRoomRepo.findMyRoomByUser(user);
        model.addAttribute("myRoom", myRoom);

        return "payment";
    }

    @PostMapping("/payment")
    public String submitPayment(@RequestParam("amount") double amount,
            @RequestParam("date") String date,
            @RequestParam("pin") String pin,
            @RequestParam("description") String description,
            RedirectAttributes redirectAttribute,
            Authentication authentication) {
        System.out.println("DEBUG: Enter payment controller post mapping*****************");
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        Payment payment = new Payment();
        List<BankAccount> bankAccountList = bankAccountRepo.findByUser(user);

        System.out.println("DEBUG: User: " + email + ", Bank Accounts: "
                + (bankAccountList != null ? bankAccountList.size() : "null"));

        BankAccount primaryAccount = bankAccountList.stream()
                .filter(BankAccount::isPrimaryAccount)
                .findFirst()
                .orElse(null);

        if (primaryAccount == null) {
            System.out.println("DEBUG: No primary account found");
            redirectAttribute.addFlashAttribute("error", "no_primary_account");
            return "redirect:/payment";
        }

        boolean pinValid = bankAccountList.stream()
                .filter(BankAccount::isPrimaryAccount)
                .anyMatch(account -> passwordEncoder.matches(pin, account.getPin()));

        if (!pinValid) {
            System.out.println("DEBUG: Invalid PIN");
            redirectAttribute.addFlashAttribute("error", "invalid_pin");
            return "redirect:/payment";
        }

        payment.setPaymentDate(date);
        payment.setAmount(amount);
        payment.setUser(user);
        payment.setDescription(description);
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setStatus("Pending");
        paymentService.save(payment); // Ensure you save the payment
        System.out.println("DEBUG: Payment saved with Pending status. ID: " + payment.getPaymentId());

        // Call the dummy payment service
        String paymentResponse = dummyPaymentService.processPayment(amount, payment.getPaymentId());
        System.out.println("DEBUG: Payment Response: " + paymentResponse);

        // Check payment response and set payment status accordingly
        if (paymentResponse.contains("successfully")) {
            payment.setStatus("Completed");
            redirectAttribute.addFlashAttribute("success", "success_payment");
        } else {
            payment.setStatus("Failed");
            redirectAttribute.addFlashAttribute("error", "payment_failed");
        }

        paymentService.save(payment); // Save the updated payment status
        System.out.println("DEBUG: Payment updated with status: " + payment.getStatus());
        return "redirect:/payment";
    }
}
