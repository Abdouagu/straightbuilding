package com.example.construction.repository;

import com.example.construction.entities.Depence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepenceRepository extends JpaRepository<Depence, Integer> {

    List<Depence> findByChantierId(Integer chantierId);
}