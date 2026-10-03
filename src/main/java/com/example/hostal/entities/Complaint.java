package com.example.hostal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String cmpType;
    private String cmpMess;
    private String date;
    @ManyToOne
    private User user;
    private String status;

    public Complaint() {
    }

    public Complaint(int id, String cmpType, String cmpMess, String date, User user, String status) {
        this.id = id;
        this.cmpType = cmpType;
        this.cmpMess = cmpMess;
        this.date = date;
        this.user = user;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCmpType() {
        return cmpType;
    }

    public void setCmpType(String cmpType) {
        this.cmpType = cmpType;
    }

    public String getCmpMess() {
        return cmpMess;
    }

    public void setCmpMess(String cmpMess) {
        this.cmpMess = cmpMess;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "id=" + id +
                ", cmpType='" + cmpType + '\'' +
                ", cmpMess='" + cmpMess + '\'' +
                ", date='" + date + '\'' +
                ", user=" + user +
                ", status='" + status + '\'' +
                '}';
    }
}
