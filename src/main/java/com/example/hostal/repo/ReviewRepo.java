package com.example.hostal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hostal.entities.Review;
import com.example.hostal.entities.User;

public interface ReviewRepo extends JpaRepository<Review, Integer> {
    Review findReviewByUser(User user);

    java.util.List<Review> findAllReviewsByUser(User user);
}
