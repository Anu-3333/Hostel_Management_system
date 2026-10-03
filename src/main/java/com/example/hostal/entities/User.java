package com.example.hostal.entities;

import jakarta.persistence.*;

@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email", unique = true)
    private String userEmail;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "role")
    private String roles;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "room_type")
    private String roomType;

    private int hostelNumber;

    private String gender;

    private String address;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "my_room_id")
    private MyRoom myRoom;

    @Transient
    private String upcomingBookingId;

    @Transient
    private String upcomingBookingDate;

    @Transient
    private String activityDate1;

    @Transient
    private String activityDate2;

    @Transient
    private String activityDate3;

    @Transient
    private int totalStudents;

    @Transient
    private int pendingRequests;

    public User() {
    }

    public User(int id, String userName, String userEmail, String userPassword, String userPhone, String roles,
            String roomNumber, String roomType, int hostelNumber, String gender, String address, MyRoom myRoom,
            String upcomingBookingId, String upcomingBookingDate, String activityDate1, String activityDate2,
            String activityDate3, int totalStudents, int pendingRequests) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userPhone = userPhone;
        this.roles = roles;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.hostelNumber = hostelNumber;
        this.gender = gender;
        this.address = address;
        this.myRoom = myRoom;
        this.upcomingBookingId = upcomingBookingId;
        this.upcomingBookingDate = upcomingBookingDate;
        this.activityDate1 = activityDate1;
        this.activityDate2 = activityDate2;
        this.activityDate3 = activityDate3;
        this.totalStudents = totalStudents;
        this.pendingRequests = pendingRequests;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
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

    public int getHostelNumber() {
        return hostelNumber;
    }

    public void setHostelNumber(int hostelNumber) {
        this.hostelNumber = hostelNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public MyRoom getMyRoom() {
        return myRoom;
    }

    public void setMyRoom(MyRoom myRoom) {
        this.myRoom = myRoom;
    }

    public String getUpcomingBookingId() {
        return upcomingBookingId;
    }

    public void setUpcomingBookingId(String upcomingBookingId) {
        this.upcomingBookingId = upcomingBookingId;
    }

    public String getUpcomingBookingDate() {
        return upcomingBookingDate;
    }

    public void setUpcomingBookingDate(String upcomingBookingDate) {
        this.upcomingBookingDate = upcomingBookingDate;
    }

    public String getActivityDate1() {
        return activityDate1;
    }

    public void setActivityDate1(String activityDate1) {
        this.activityDate1 = activityDate1;
    }

    public String getActivityDate2() {
        return activityDate2;
    }

    public void setActivityDate2(String activityDate2) {
        this.activityDate2 = activityDate2;
    }

    public String getActivityDate3() {
        return activityDate3;
    }

    public void setActivityDate3(String activityDate3) {
        this.activityDate3 = activityDate3;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(int pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", roles='" + roles + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomType='" + roomType + '\'' +
                ", hostelNumber=" + hostelNumber +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", upcomingBookingId='" + upcomingBookingId + '\'' +
                ", upcomingBookingDate='" + upcomingBookingDate + '\'' +
                ", activityDate1='" + activityDate1 + '\'' +
                ", activityDate2='" + activityDate2 + '\'' +
                ", activityDate3='" + activityDate3 + '\'' +
                ", totalStudents=" + totalStudents +
                ", pendingRequests=" + pendingRequests +
                '}';
    }
}
