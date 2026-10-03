package com.example.hostal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class MyRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true)
    private String bookingId;
    @Column(unique = true)
    private String roomNumber;
    private String roomType;
    private String status;
    private String date;
    private String additionalInfo;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    public MyRoom() {
    }

    public MyRoom(int id, String bookingId, String roomNumber, String roomType, String status, String date,
            String additionalInfo, User user) {
        this.id = id;
        this.bookingId = bookingId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = status;
        this.date = date;
        this.additionalInfo = additionalInfo;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "MyRoom{" +
                "id=" + id +
                ", bookingId='" + bookingId + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomType='" + roomType + '\'' +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}
