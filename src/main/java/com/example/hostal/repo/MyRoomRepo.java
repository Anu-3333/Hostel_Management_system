package com.example.hostal.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hostal.entities.MyRoom;
import com.example.hostal.entities.User;

public interface MyRoomRepo extends JpaRepository<MyRoom, Integer> {
    MyRoom findById(int id);

    MyRoom findMyRoomByUser(User user);

    java.util.List<MyRoom> findAllMyRoomsByUser(User user);

    long countByStatus(String status);

    List<MyRoom> findByStatus(String status);

    MyRoom findRoomByRoomNumber(String roomNumber);

    Optional<MyRoom> findByRoomNumber(String roomNumber);

}
