package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaForm {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombre;

    @NotBlank(message = "La especie es requerida")
    @Size(max = 50, message = "La especie no puede superar los 50 caracteres")
    private String especie;

    @Size(max = 80, message = "La raza no puede superar los 80 caracteres")
    private String raza;

    @NotBlank(message = "La edad aproximada es requerida")
    @Size(max = 50, message = "La edad aproximada no puede superar los 50 caracteres")
    private String edadAproximada;

    @NotBlank(message = "El sexo es requerido")
    @Size(max = 20, message = "El sexo no puede superar los 20 caracteres")
    private String sexo;

    @NotBlank(message = "La descripción es requerida")
    private String descripcion;

    @Size(max = 255, message = "La URL de la imagen no puede superar los 255 caracteres")
    private String imagenUrl;
}
