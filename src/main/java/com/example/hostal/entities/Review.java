package com.example.hostal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String message;
    private int rating;
    private String date;
    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY)
    private User user;

    public Review() {
    }

    public Review(int id, String message, int rating, String date, User user) {
        this.id = id;
        this.message = message;
        this.rating = rating;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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
        return "Review{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", rating=" + rating +
                ", date='" + date + '\'' +
                ", user=" + user +
                '}';
    }
}
