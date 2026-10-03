package com.example.hostal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "changes")
public class Changes {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String notify;
    private String type; // use ke liye ya admin ke liye
    private String date;
    private int userId;
    @Column(nullable = false)
    private boolean isnew = true;

    public Changes() {
    }

    public Changes(int id, String notify, String type, String date, int userId, boolean isnew) {
        this.id = id;
        this.notify = notify;
        this.type = type;
        this.date = date;
        this.userId = userId;
        this.isnew = isnew;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isIsnew() {
        return isnew;
    }

    public void setIsnew(boolean isnew) {
        this.isnew = isnew;
    }

    @Override
    public String toString() {
        return "Changes{" +
                "id=" + id +
                ", notify='" + notify + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", userId=" + userId +
                ", isnew=" + isnew +
                '}';
    }
}
