package com.example.hostal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "activity")
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String activeTime;
    private String date;
    @ManyToOne
    private User user;

    public Activite() {
    }

    public Activite(int id, String activeTime, String date, User user) {
        this.id = id;
        this.activeTime = activeTime;
        this.date = date;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
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
        return "Activite{" +
                "id=" + id +
                ", activeTime='" + activeTime + '\'' +
                ", date='" + date + '\'' +
                ", user=" + user +
                '}';
    }
}
