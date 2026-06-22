package com.example.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MascotaFormTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidMascotaForm() {
        MascotaForm form = new MascotaForm(
                "Bobby",
                "Perro",
                "Labrador",
                "3 años",
                "MACHO",
                "Un perro amigable",
                "http://example.com/image.jpg"
        );

        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        assertTrue(violations.isEmpty(), "Formulario válido no debería tener violaciones");
    }

    @Test
    public void testBlankFields() {
        MascotaForm form = new MascotaForm(
                "", // nombre en blanco
                "   ", // especie en blanco
                "Labrador",
                "", // edadAproximada en blanco
                "", // sexo en blanco
                "   ", // descripcion en blanco
                "http://example.com/image.jpg"
        );

        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        // Esperamos al menos 5 violaciones (nombre, especie, edadAproximada, sexo, descripcion)
        assertTrue(violations.size() >= 5);

        boolean hasNombreError = false;
        boolean hasEspecieError = false;
        boolean hasEdadError = false;
        boolean hasSexoError = false;
        boolean hasDescripcionError = false;

        for (ConstraintViolation<MascotaForm> violation : violations) {
            String property = violation.getPropertyPath().toString();
            if ("nombre".equals(property)) hasNombreError = true;
            if ("especie".equals(property)) hasEspecieError = true;
            if ("edadAproximada".equals(property)) hasEdadError = true;
            if ("sexo".equals(property)) hasSexoError = true;
            if ("descripcion".equals(property)) hasDescripcionError = true;
        }

        assertTrue(hasNombreError, "Debería fallar por nombre en blanco");
        assertTrue(hasEspecieError, "Debería fallar por especie en blanco");
        assertTrue(hasEdadError, "Debería fallar por edadAproximada en blanco");
        assertTrue(hasSexoError, "Debería fallar por sexo en blanco");
        assertTrue(hasDescripcionError, "Debería fallar por descripcion en blanco");
    }

    @Test
    public void testSizeExceeded() {
        String longNombre = "a".repeat(81);
        String longEspecie = "a".repeat(51);
        String longRaza = "a".repeat(81);
        String longEdad = "a".repeat(51);
        String longSexo = "a".repeat(21);
        String longUrl = "a".repeat(256);

        MascotaForm form = new MascotaForm(
                longNombre,
                longEspecie,
                longRaza,
                longEdad,
                longSexo,
                "Descripción válida",
                longUrl
        );

        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        // Esperamos 6 violaciones por exceso de tamaño (nombre, especie, raza, edadAproximada, sexo, imagenUrl)
        assertEquals(6, violations.size());
    }
}
