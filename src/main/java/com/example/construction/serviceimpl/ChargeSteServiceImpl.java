package com.example.construction.serviceimpl;

import com.example.construction.dto.ChargeSteDTO;
import com.example.construction.entities.ChargeSte;
import com.example.construction.repository.ChargeSteRepository;
import com.example.construction.service.ChargeSteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargeSteServiceImpl implements ChargeSteService {

    private final ChargeSteRepository chargeSteRepository;

    @Override
    @Transactional
    public ChargeSteDTO create(ChargeSteDTO dto) {
        ChargeSte c = ChargeSte.builder()
                .total(dto.getTotal())
                .date(dto.getDate())
                .commentaire(dto.getCommentaire())
                .build();
        return mapToDTO(chargeSteRepository.save(c));
    }

    @Override
    @Transactional
    public ChargeSteDTO update(Integer id, ChargeSteDTO dto) {
        ChargeSte c = chargeSteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charge introuvable"));
        c.setTotal(dto.getTotal());
        c.setDate(dto.getDate());
        c.setCommentaire(dto.getCommentaire());
        return mapToDTO(chargeSteRepository.save(c));
    }

    @Override
    @Transactional(readOnly = true)
    public ChargeSteDTO getById(Integer id) {
        return mapToDTO(chargeSteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charge introuvable")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargeSteDTO> findAll() {
        return chargeSteRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!chargeSteRepository.existsById(id))
            throw new IllegalArgumentException("Charge introuvable");
        chargeSteRepository.deleteById(id);
    }

    private ChargeSteDTO mapToDTO(ChargeSte c) {
        return ChargeSteDTO.builder()
                .id(c.getId())
                .total(c.getTotal())
                .date(c.getDate())
                .commentaire(c.getCommentaire())
                .build();
    }
}