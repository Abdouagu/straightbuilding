package com.example.construction.service;

import com.example.construction.dto.DepenceDTO;

import java.util.List;

public interface DepenceService {

    DepenceDTO create(DepenceDTO dto);
    DepenceDTO update(Integer id, DepenceDTO dto);
    DepenceDTO getById(Integer id);
    List<DepenceDTO> listByChantier(Integer chantierId);
    void delete(Integer id);
}