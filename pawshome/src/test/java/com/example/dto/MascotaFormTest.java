package com.example.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

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
                "3",
                "Macho",
                "Un perro muy amigable",
                "http://example.com/image.jpg"
        );

        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        assertTrue(violations.isEmpty(), "Formulario válido no debería tener violaciones");
    }

    @Test
    public void testBlankFields() {
        MascotaForm form = new MascotaForm(
                "",        // nombre en blanco
                "   ",     // especie en blanco
                "Labrador",
                null,      // edadAproximada nula
                "",        // sexo en blanco
                "   ",     // descripcion en blanco
                "http://example.com/image.jpg"
        );

        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(fields.contains("nombre"), "Debería fallar por nombre en blanco");
        assertTrue(fields.contains("especie"), "Debería fallar por especie en blanco");
        assertTrue(fields.contains("edadAproximada"), "Debería fallar por edadAproximada nula");
        assertTrue(fields.contains("sexo"), "Debería fallar por sexo en blanco");
        assertTrue(fields.contains("descripcion"), "Debería fallar por descripcion en blanco");
    }

    @Test
    public void testEdadNegativaFalla() {
        MascotaForm form = new MascotaForm(
                "Luna", "Gato", null, "-1", "Hembra", "Descripción válida aquí", null
        );
        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(fields.contains("edadAproximada"), "Debería fallar por edad negativa");
    }

    @Test
    public void testEdadSuperiorA30Falla() {
        MascotaForm form = new MascotaForm(
                "Luna", "Gato", null, "31", "Hembra", "Descripción válida aquí", null
        );
        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(fields.contains("edadAproximada"), "Debería fallar por edad superior a 30");
    }

    @Test
    public void testSexoInvalidoFalla() {
        MascotaForm form = new MascotaForm(
                "Max", "Perro", null, "2", "MACHO", "Descripción válida aquí", null
        );
        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(fields.contains("sexo"), "Debería fallar por sexo inválido (solo Macho/Hembra)");
    }

    @Test
    public void testUrlImagenInvalidaFalla() {
        MascotaForm form = new MascotaForm(
                "Max", "Perro", null, "2", "Macho", "Descripción válida aquí", "no-es-una-url"
        );
        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(fields.contains("imagenUrl"), "Debería fallar por URL de imagen inválida");
    }

    @Test
    public void testEdadConComaDecimalEsValida() {
        MascotaForm form = new MascotaForm(
                "Milo", "Gato", null, "0,5", "Macho", "Descripción válida aquí", null
        );

        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertFalse(fields.contains("edadAproximada"), "La edad 0,5 debería ser válida");
    }

    @Test
    public void testEdadConPuntoDecimalFalla() {
        MascotaForm form = new MascotaForm(
                "Milo", "Gato", null, "0.5", "Macho", "Descripción válida aquí", null
        );

        Set<ConstraintViolation<MascotaForm>> violations = validator.validate(form);
        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(fields.contains("edadAproximada"), "La edad con punto decimal debería fallar");
    }
}
