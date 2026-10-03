package com.example.hostal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hostal.entities.Feedback;
import com.example.hostal.entities.User;

public interface FeedbackRepo extends JpaRepository<Feedback, Integer> {
    Feedback findByUser(User user);

    java.util.List<Feedback> findAllFeedbacksByUser(User user);
}
