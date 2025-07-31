package com.example.construction.serviceimpl;

import com.example.construction.DTO.DepenceDTO;
import com.example.construction.entities.chantier;
import com.example.construction.entities.Depence;
import com.example.construction.repository.Chantierepo;
import com.example.construction.repository.DepenceRepository;
import com.example.construction.service.DepenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepenceServiceImpl implements DepenceService {

    private final DepenceRepository depenceRepository;
    private final Chantierepo chantierRepository;

    @Override
    @Transactional
    public DepenceDTO create(DepenceDTO dto) {
        chantier chantier = chantierRepository.findById(dto.getIdChantier())
                .orElseThrow(() -> new IllegalArgumentException("Chantier not found"));

        Depence depence = Depence.builder()
                .total(dto.getTotal())
                .date(dto.getDate())
                .commentaire(dto.getCommentaire())
                .chantier(chantier)
                .build();

        depence = depenceRepository.save(depence);
        return mapToDTO(depence);
    }

    @Override
    @Transactional
    public DepenceDTO update(Integer id, DepenceDTO dto) {
        Depence depence = depenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Depence not found"));

        chantier chantier = chantierRepository.findById(dto.getIdChantier())
                .orElseThrow(() -> new IllegalArgumentException("Chantier not found"));

        depence.setTotal(dto.getTotal());
        depence.setDate(dto.getDate());
        depence.setCommentaire(dto.getCommentaire());
        depence.setChantier(chantier);

        return mapToDTO(depenceRepository.save(depence));
    }

    @Override
    @Transactional(readOnly = true)
    public DepenceDTO getById(Integer id) {
        return depenceRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Depence not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepenceDTO> listByChantier(Integer chantierId) {
        return depenceRepository.findByChantierId(chantierId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!depenceRepository.existsById(id)) {
            throw new IllegalArgumentException("Depence not found");
        }
        depenceRepository.deleteById(id);
    }

    private DepenceDTO mapToDTO(Depence d) {
        return DepenceDTO.builder()
                .id(d.getId())
                .total(d.getTotal())
                .date(d.getDate())
                .commentaire(d.getCommentaire())
                .idChantier(d.getChantier().getId())
                .build();
    }
}