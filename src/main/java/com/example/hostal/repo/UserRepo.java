package com.example.hostal.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.hostal.entities.User;

public interface UserRepo extends JpaRepository<User, Integer> {
    User findUserByUserEmail(String email);

    List<User> findUserByRoles(String roles);

    User findById(int id);

    List<User> findUserByHostelNumber(int hostelNumber);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE User u SET u.myRoom = null, u.roomNumber = 'N/A', u.roomType = 'N/A' WHERE u.id = :id")
    void resetUserRoom(@org.springframework.data.repository.query.Param("id") int id);

    User findByMyRoomId(int id);
}
