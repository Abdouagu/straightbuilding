package com.example.construction.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointageDTO {
    private Long id;
    private Integer ouvrierId;  // Changé de int à Integer
    private Integer chantierId; // Changé de int à Integer
    private LocalDate date;
    private boolean present;
    private Float heuresTravaillees;

    @Override
    public String toString() {
        return "PointageDTO{" +
                "id=" + id +
                ", ouvrierId=" + ouvrierId +
                ", chantierId=" + chantierId +
                ", date=" + date +
                ", present=" + present +
                ", heuresTravaillees=" + heuresTravaillees +
                '}';
    }
}
