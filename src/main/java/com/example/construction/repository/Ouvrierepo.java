package com.example.construction.repository;

import com.example.construction.entities.Type;
import com.example.construction.entities.ouvrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface Ouvrierepo extends JpaRepository<ouvrier, Integer> {
     Optional<ouvrier> findByCin(String cin);
     boolean existsByCin(String cin);
     List<ouvrier> findByNomContainingIgnoreCase(String nom);
     List<ouvrier> findByType(Type type);
     List<ouvrier> findByChantier_Id(int chantierId);
}