package com.example.construction.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargeSteDTO {
    private Integer id;
    private BigDecimal total;
    private LocalDate date;
    private String commentaire;
}