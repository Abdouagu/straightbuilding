package com.example.construction.controller;

import com.example.construction.dto.ChargeSteDTO;
import com.example.construction.service.ChargeSteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charges")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChargeSteController {

    private final ChargeSteService chargeSteService;

    @PostMapping
    public ResponseEntity<ChargeSteDTO> create(@RequestBody ChargeSteDTO dto) {
        return new ResponseEntity<>(chargeSteService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargeSteDTO> update(@PathVariable Integer id,
                                               @RequestBody ChargeSteDTO dto) {
        return ResponseEntity.ok(chargeSteService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargeSteDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(chargeSteService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ChargeSteDTO>> findAll() {
        return ResponseEntity.ok(chargeSteService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        chargeSteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}