package com.example.hostal.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private User user;

    @OneToOne
    @JoinColumn(name = "room_id", nullable = false)
    private MyRoom room;

    private String checkoutRequestDate;
    private String reason;
    private String status;

    public Checkout() {
    }

    public Checkout(Long id, User user, MyRoom room, String checkoutRequestDate, String reason, String status) {
        this.id = id;
        this.user = user;
        this.room = room;
        this.checkoutRequestDate = checkoutRequestDate;
        this.reason = reason;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MyRoom getRoom() {
        return room;
    }

    public void setRoom(MyRoom room) {
        this.room = room;
    }

    public String getCheckoutRequestDate() {
        return checkoutRequestDate;
    }

    public void setCheckoutRequestDate(String checkoutRequestDate) {
        this.checkoutRequestDate = checkoutRequestDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Checkout{" +
                "id=" + id +
                ", user=" + user +
                ", room=" + room +
                ", checkoutRequestDate='" + checkoutRequestDate + '\'' +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
