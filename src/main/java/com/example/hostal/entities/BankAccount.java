package com.example.hostal.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "card_number", nullable = false, unique = true)
    private String cardNumber;

    @Column(name = "card_holder_name", nullable = false)
    private String cardHolderName;

    @Column(name = "expiry_date", nullable = false)
    private String expiryDate;

    @Column(name = "cvv", nullable = false)
    private String cvv;

    @Column(name = "account_type", nullable = false)
    private String accountType;

    @ManyToOne
    private User user;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isPrimaryAccount = false;

    private String pin;

    @Column(name = "email")
    private String email;

    public BankAccount() {
    }

    public BankAccount(int id, String cardNumber, String cardHolderName, String expiryDate, String cvv,
            String accountType, User user, boolean isPrimaryAccount, String pin, String email) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.accountType = accountType;
        this.user = user;
        this.isPrimaryAccount = isPrimaryAccount;
        this.pin = pin;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPrimaryAccount() {
        return isPrimaryAccount;
    }

    public void setPrimaryAccount(boolean primaryAccount) {
        isPrimaryAccount = primaryAccount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "id=" + id +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", cvv='" + cvv + '\'' +
                ", accountType='" + accountType + '\'' +
                ", user=" + user +
                ", isPrimaryAccount=" + isPrimaryAccount +
                ", pin='" + pin + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
