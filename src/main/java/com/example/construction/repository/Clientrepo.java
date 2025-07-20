package com.example.construction.repository;


import com.example.construction.entities.client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Clientrepo extends JpaRepository<client, Integer> {

    Optional<client> findByEmail(String email);
    Optional<client> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
