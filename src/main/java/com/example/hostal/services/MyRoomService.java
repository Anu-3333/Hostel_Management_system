package com.example.hostal.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hostal.entities.MyRoom;
import com.example.hostal.entities.User;
import com.example.hostal.repo.MyRoomRepo;
import com.example.hostal.repo.UserRepo;

@Service
public class MyRoomService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MyRoomRepo myRoomRepo;

    public List<MyRoom> getAllRooms() {
        return myRoomRepo.findAll();
    }

    public MyRoom getRoomById(int id) {
        return myRoomRepo.findById(id);
    }

    public MyRoom saveRoom(MyRoom room) {
        return myRoomRepo.save(room);
    }

    public void deleteRoom(int id) {
        myRoomRepo.deleteById(id);
    }

    public List<User> getPendingRequestUser() {
        List<MyRoom> myRoomsList = myRoomRepo.findAll();
        List<User> userList = new ArrayList<>();
        for (MyRoom mr : myRoomsList) {
            if (mr.getStatus().equals("Pending")) {
                User user = mr.getUser();
                if (user != null) {
                    userList.add(user);
                }
            }
        }
        return userList;
    }

    public boolean isValidRoom(MyRoom myRoom) {
        String roomNumber = myRoom.getRoomNumber();
        List<MyRoom> list = myRoomRepo.findAll();
        for (MyRoom mr : list) {
            if (mr.getId() == myRoom.getId())
                continue;
            if (mr.getRoomNumber().equals(roomNumber))
                return false;
        }
        return true;
    }

    public int lastRoomThroughHostel(int hostelNo) {
        int minRoom;
        int maxRoom;

        if (hostelNo == 1) {
            minRoom = 1;
            maxRoom = 300;
        } else if (hostelNo == 2) {
            minRoom = 301;
            maxRoom = 600;
        } else {
            // Default fallback if unknown hostel number (though logic suggests only 1 or 2)
            return -1;
        }

        // Fetch all rooms from MyRoom repo to ensure we check against the actual table
        // that enforces constraints
        List<MyRoom> allRooms = myRoomRepo.findAll();
        java.util.Set<Integer> occupiedRooms = new java.util.HashSet<>();

        for (MyRoom room : allRooms) {
            try {
                if (room.getRoomNumber() != null && !room.getRoomNumber().isEmpty()) {
                    occupiedRooms.add(Integer.parseInt(room.getRoomNumber()));
                }
            } catch (NumberFormatException e) {
                // Ignore invalid room numbers
            }
        }

        // Find the first available room number in the range
        for (int i = minRoom; i <= maxRoom; i++) {
            if (!occupiedRooms.contains(i)) {
                return i;
            }
        }

        // Fallback if full
        return -1;
    }

}
