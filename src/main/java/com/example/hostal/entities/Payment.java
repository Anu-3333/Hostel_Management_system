package com.example.hostal.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String paymentId;

    @Column(nullable = false)
    // @Temporal(TemporalType.DATE)
    private String paymentDate;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String description;

    // Additional fields can be added here if needed

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public Payment() {
    }

    public Payment(int id, String paymentId, String paymentDate, Double amount, String status, String description,
            User user) {
        this.id = id;
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.status = status;
        this.description = description;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentId='" + paymentId + '\'' +
                ", paymentDate='" + paymentDate + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", user=" + user +
                '}';
    }
}
