package com.example.hostal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hostal.entities.BankAccount;
import com.example.hostal.entities.User;
import com.example.hostal.repo.BankAccountRepo;
import com.example.hostal.repo.UserRepo;
import com.example.hostal.services.BankAccountService;

@Controller
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BankAccountRepo bankAccountRepo;

    @PostMapping("/addbankaccount")
    public String addBankAccount(@ModelAttribute("bankAccount") BankAccount bankAccount, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        System.out.println("runn post mapping of add bank account*****************************");
        if (bankAccountService.isBankPresent(bankAccount.getCardNumber())) {
            redirectAttributes.addFlashAttribute("error", "Card already exists!");
            return "redirect:/bank_account";
        }

        String email = authentication.getName();
        User user = userRepo.findUserByUserEmail(email);

        // Check if user has no accounts, then set this as primary
        if (bankAccountRepo.findByUser(user).isEmpty()) {
            bankAccount.setPrimaryAccount(true);
        }

        // check if pin is present
        if (bankAccount.getPin() == null || bankAccount.getPin().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "PIN cannot be null or empty.");
            return "redirect:/bank_account";
        }
        // Default expiry date since it's removed from UI
        if (bankAccount.getExpiryDate() == null || bankAccount.getExpiryDate().isEmpty()) {
            bankAccount.setExpiryDate("12/99");
        }

        bankAccount.setAccountType("Saving");
        bankAccount.setUser(user);
        bankAccount.setPin(passwordEncoder.encode(bankAccount.getPin()));
        bankAccountService.saveBankAccount(bankAccount);
        redirectAttributes.addFlashAttribute("success", "Bank account added successfully!");
        return "redirect:/bank_account";
    }

    @PostMapping("/deletebankaccount/{id}")
    public String deleteBankAccount(@PathVariable int id, RedirectAttributes redirectAttribute) {

        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        if (bankAccount.isPrimaryAccount()) {
            User user = bankAccount.getUser();
            List<BankAccount> userAccounts = bankAccountRepo.findByUser(user);
            if (userAccounts.size() > 1) {
                redirectAttribute.addFlashAttribute("error", "delete_primary_account");
                return "redirect:/payment";
            }
        }

        boolean isDeleted = bankAccountService.deleteBankAccount(id);

        if (isDeleted) {
            redirectAttribute.addAttribute("success", "Card Deleted Successfully!");
        } else {
            redirectAttribute.addAttribute("error", "Card Deletion Failed!");
        }

        // List<BankAccount> bankAccounts = bankAccountService.getAllBankAccounts();
        // redirectAttribute.addAttribute("bankAccounts", bankAccounts);
        redirectAttribute.addFlashAttribute("error", "delete_primary_account");
        return "redirect:/payment";
    }

    @PostMapping("/updatebankaccount/{id}")
    public String updateBankAccount(@PathVariable int id, RedirectAttributes redirectAttribute,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepo.findUserByUserEmail(email);

        List<BankAccount> bankAccountList = bankAccountRepo.findByUser(user);
        BankAccount primaryAccount = bankAccountList.stream()
                .filter(BankAccount::isPrimaryAccount)
                .findFirst()
                .orElse(null);

        // If primaryAccount is null, we can still proceed to set the new one as
        // primary.

        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        bankAccount.setPrimaryAccount(true);

        // set previous prime account to secondary account if it exists
        if (primaryAccount != null) {
            primaryAccount.setPrimaryAccount(false);
            bankAccountService.saveBankAccount(primaryAccount);
        }

        bankAccountService.saveBankAccount(bankAccount);

        redirectAttribute.addFlashAttribute("success", "success_update");
        return "redirect:/payment";

    }

}
