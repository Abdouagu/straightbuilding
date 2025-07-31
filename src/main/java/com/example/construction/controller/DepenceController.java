package com.example.construction.controller;

import com.example.construction.DTO.DepenceDTO;
import com.example.construction.service.DepenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depences")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DepenceController {

    private final DepenceService depenceService;

    @PostMapping
    public ResponseEntity<DepenceDTO> create(@RequestBody DepenceDTO dto) {
        return new ResponseEntity<>(depenceService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepenceDTO> update(@PathVariable Integer id,
                                             @RequestBody DepenceDTO dto) {
        return ResponseEntity.ok(depenceService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepenceDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(depenceService.getById(id));
    }

    @GetMapping("/chantier/{chantierId}")
    public ResponseEntity<List<DepenceDTO>> listByChantier(@PathVariable Integer chantierId) {
        return ResponseEntity.ok(depenceService.listByChantier(chantierId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        depenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}