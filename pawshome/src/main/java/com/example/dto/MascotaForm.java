package com.example.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "La edad aproximada es requerida")
    @Pattern(
            regexp = "^\\s*$|^(([0-9]|[12][0-9])(?:,[0-9])?|30(?:,0)?)$",
            message = "La edad debe estar entre 0 y 30; usa coma para decimales (ej: 0,5)"
    )
    private String edadAproximada;

    @NotBlank(message = "El sexo es requerido")
    @Pattern(regexp = "^(Macho|Hembra)$", message = "El sexo debe ser Macho o Hembra")
    private String sexo;

    @NotBlank(message = "La descripción es requerida")
    @Pattern(
            regexp = "(?s)^\\s*$|^.{10,255}$",
            message = "La descripción debe tener entre 10 y 255 caracteres"
    )
    private String descripcion;

    @URL(message = "La URL de la imagen no es válida")
    @Size(max = 255, message = "La URL de la imagen no puede superar los 255 caracteres")
    private String imagenUrl;

    public MascotaForm(String nombre, String especie, String raza, String edadAproximada,
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
