package com.example.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
public class MascotaForm {

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s\\-]+$",
             message = "El nombre solo puede contener letras, espacios y guiones")
    private String nombre;

    @NotBlank(message = "La especie es requerida")
    @Size(min = 2, max = 40, message = "La especie debe tener entre 2 y 40 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]+$",
             message = "La especie solo puede contener letras")
    private String especie;

    @Size(max = 60, message = "La raza no puede superar los 60 caracteres")
    @Pattern(regexp = "^$|^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s\\-]{2,60}$",
             message = "La raza debe tener al menos 2 caracteres y solo puede contener letras, espacios y guiones")
    private String raza;

    @NotNull(message = "La edad aproximada es requerida")
    @DecimalMin(value = "0.0", message = "La edad no puede ser negativa")
    @DecimalMax(value = "30.0", message = "La edad no puede superar los 30 años")
    @Digits(integer = 2, fraction = 1, message = "La edad admite máximo 1 decimal (ej: 1.5)")
    private Double edadAproximada;

    @NotBlank(message = "El sexo es requerido")
    @Pattern(regexp = "^(Macho|Hembra)$", message = "El sexo debe ser Macho o Hembra")
    private String sexo;

    @NotBlank(message = "La descripción es requerida")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    private String descripcion;

    @URL(message = "La URL de la imagen no es válida")
    @Size(max = 255, message = "La URL de la imagen no puede superar los 255 caracteres")
    private String imagenUrl;

    public MascotaForm(String nombre, String especie, String raza, Double edadAproximada,
                       String sexo, String descripcion, String imagenUrl) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edadAproximada = edadAproximada;
        this.sexo = sexo;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
    }
}
