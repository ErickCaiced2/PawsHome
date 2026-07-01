package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaFiltroDTO {
    private String nombre;
    private String especie;
    private String raza;
    private String sexo;
}
