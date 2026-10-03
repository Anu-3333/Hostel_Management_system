package com.example.hostal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hostal.entities.Activite;
import com.example.hostal.entities.User;

public interface ActiveRepo extends JpaRepository<Activite, Integer> {

    Activite findByUser(User user);

    java.util.List<Activite> findAllActivitesByUser(User user);

}
