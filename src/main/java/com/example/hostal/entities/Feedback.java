package com.example.hostal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String message;
    private String date;
    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    private User user;

    public Feedback() {
    }

    public Feedback(int id, String message, String date, User user) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", user=" + user +
                '}';
    }
}
