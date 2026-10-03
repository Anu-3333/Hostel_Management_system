package com.example.hostal.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hostal.entities.Changes;
import com.example.hostal.entities.MyRoom;
import com.example.hostal.entities.User;
import com.example.hostal.repo.ChangesRepo;
import com.example.hostal.repo.MyRoomRepo;
import com.example.hostal.repo.UserRepo;
import com.example.hostal.services.MyRoomService;

@Controller
public class PendingApproveRequest {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MyRoomRepo myRoomRepo;

    @Autowired
    private MyRoomService myRoomService;

    @Autowired
    private ChangesRepo changesRepo;

    @PostMapping("/admin/approverequest")
    public String approveRequest(@RequestParam("email") String email,
            @RequestParam(value = "roomNumber", required = false) String roomNumber,
            @RequestParam(value = "roomType", required = false) String roomType) {
        try {
            System.out.println("Pending request controller run....................");
            User user = userRepo.findUserByUserEmail(email);
            MyRoom myRoom = myRoomRepo.findMyRoomByUser(user);

            // if room is not valid or not available
            if (!myRoomService.isValidRoom(myRoom)) {
                myRoom.setStatus("This room is not available");
                myRoomRepo.save(myRoom);
                return "redirect:/requestformadmin";
            }

            // Assign Room Type: Prefer Admin input, then existing preference, then default
            if (roomType != null && !roomType.isEmpty()) {
                myRoom.setRoomType(roomType);
            } else if (myRoom.getRoomType() == null || myRoom.getRoomType().isEmpty()) {
                myRoom.setRoomType("Single");
            }

            // Assign Room Number: Prefer Admin input, then auto-assign
            // NOTE: Ideally we should validate if the manually entered room number exists
            // and is empty,
            // but ensuring the functionality first as per request.
            if (roomNumber != null && !roomNumber.isEmpty()) {
                myRoom.setRoomNumber(roomNumber);
                user.setRoomNumber(roomNumber);
            } else if (myRoom.getRoomNumber() == null || myRoom.getRoomNumber().isEmpty()) {
                int hostelNum = user.getHostelNumber() > 0 ? user.getHostelNumber() : 1;
                int assignedRoomNo = myRoomService.lastRoomThroughHostel(hostelNum);

                if (assignedRoomNo != -1) {
                    myRoom.setRoomNumber(String.valueOf(assignedRoomNo));
                    user.setRoomNumber(String.valueOf(assignedRoomNo));
                } else {
                    System.out.println("Warning: No rooms available for hostel " + hostelNum);
                }
            } else {
                // Room number already exists (e.g. from user registration if they picked one,
                // or previously set)
                // Just sync to user profile to be safe
                user.setRoomNumber(myRoom.getRoomNumber());
            }

            // Sync status and other details
            user.setRoomType(myRoom.getRoomType());
            userRepo.save(user);

            // [NEW] Generate Booking ID if missing

            // [NEW] Generate Booking ID if missing
            if (myRoom.getBookingId() == null || myRoom.getBookingId().isEmpty()) {
                java.util.Random random = new java.util.Random();
                int x = random.nextInt(10, 100);
                int y = random.nextInt(101, 200);
                String bookingId = "BKNo" + x + "Id" + y;
                myRoom.setBookingId(bookingId);
            }

            System.out.println("\n" + myRoom.getStatus() + "\n");
            System.out.println("roomid: " + myRoom.getId());
            myRoom.setStatus("Occupied");
            myRoomRepo.save(myRoom);
            System.out.println(myRoom.getStatus());
            System.out.println("pending approved...............");
            // update changes
            Changes changes = new Changes();
            changes.setDate(getCurrentDate());
            System.out.println("user id: " + user.getId());
            changes.setUserId(user.getId());
            System.out.println("user email: " + email);
            changes.setNotify("Room Status Changed! Approved " + myRoom.getBookingId());
            changes.setType("User");
            changesRepo.save(changes);

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

        return "redirect:/requestformadmin";

    }

    public String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

}
