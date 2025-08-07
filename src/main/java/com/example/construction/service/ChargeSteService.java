package com.example.construction.service;

import com.example.construction.DTO.ChargeSteDTO;

import java.util.List;

public interface ChargeSteService {
    ChargeSteDTO create(ChargeSteDTO dto);
    ChargeSteDTO update(Integer id, ChargeSteDTO dto);
    ChargeSteDTO getById(Integer id);
    List<ChargeSteDTO> findAll();
    void delete(Integer id);
}