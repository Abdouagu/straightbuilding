package com.example.construction.repository;

import com.example.construction.entities.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Userepo extends JpaRepository<user, Integer> {
    Optional<user> findByUsername(String username);
    Optional<user> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}