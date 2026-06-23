package com.example.controller;

import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import com.example.model.RolUsuario;
import com.example.model.Usuario;
import com.example.service.MascotaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * TDD — Tests para MascotaController (TASK 3.4).
 *
 * Estrategia: usamos MockitoExtension (sin Spring Context) para mantener
 * las pruebas rápidas y sin dependencias de infraestructura.
 * El Model y Principal se pasan como mocks, igual que el servicio.
 */
@ExtendWith(MockitoExtension.class)
class MascotaControllerTest {

    @Mock
    private MascotaService mascotaService;

    @Mock
    private Model model;

    @InjectMocks
    private MascotaController mascotaController;

    private Usuario administrador;
    private List<Mascota> mascotasDelAdmin;

    @BeforeEach
    void setUp() {
        // Administrador de prueba con rol correcto
        administrador = new Usuario();
        administrador.setId(10L);
        administrador.setNombre("Refugio Admin");
        administrador.setCorreo("refugio@pawshome.com");
        administrador.setPasswordHash("hash");
        administrador.setRol(RolUsuario.ADMINISTRADOR_REFUGIO);

        // Mascotas pertenecientes solo a este administrador
        Mascota m1 = crearMascota(1L, "Luna", administrador, EstadoMascota.DISPONIBLE);
        Mascota m2 = crearMascota(2L, "Max",  administrador, EstadoMascota.NO_DISPONIBLE);
        mascotasDelAdmin = List.of(m1, m2);
    }

    // ── TASK 3.4 ──────────────────────────────────────────────────────────────

    @Test
    void gestionAdministrador_retornaVistaCorrecta() {
        // Arrange: el servicio devuelve la lista del admin con id=10
        when(mascotaService.listarPorAdministrador(10L)).thenReturn(mascotasDelAdmin);

        // Act
        // TODO: cuando Spring Security esté listo, principal vendrá del SecurityContext.
        // Por ahora pasamos el id del admin directamente al servicio.
        String vista = mascotaController.gestionAdministrador(10L, model);

        // Assert: la vista lógica devuelta debe ser "mascotas/gestion"
        assertThat(vista).isEqualTo("mascotas/gestion");
    }

    @Test
    void gestionAdministrador_agregaMascotasAlModelo() {
        // Arrange
        when(mascotaService.listarPorAdministrador(10L)).thenReturn(mascotasDelAdmin);

        // Act
        mascotaController.gestionAdministrador(10L, model);

        // Assert: el modelo recibe el atributo "mascotas" con la lista filtrada
        verify(model).addAttribute("mascotas", mascotasDelAdmin);
    }

    @Test
    void gestionAdministrador_soloConsultaLasMascotasDeEseAdministrador() {
        // Arrange
        when(mascotaService.listarPorAdministrador(10L)).thenReturn(mascotasDelAdmin);

        // Act
        mascotaController.gestionAdministrador(10L, model);

        // Assert: el servicio recibe exactamente el id del administrador autenticado,
        // garantizando que no se filtran mascotas de otros administradores a nivel de query
        verify(mascotaService, times(1)).listarPorAdministrador(10L);
        verify(mascotaService, never()).listarPorAdministrador(argThat(id -> !id.equals(10L)));
    }

    @Test
    void gestionAdministrador_conListaVacia_retornaVistaIgual() {
        // Arrange: admin sin mascotas registradas aún
        when(mascotaService.listarPorAdministrador(10L)).thenReturn(List.of());

        // Act
        String vista = mascotaController.gestionAdministrador(10L, model);

        // Assert: la vista se sigue devolviendo; la lista vacía la maneja la vista
        assertThat(vista).isEqualTo("mascotas/gestion");
        verify(model).addAttribute("mascotas", List.of());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Mascota crearMascota(Long id, String nombre, Usuario admin, EstadoMascota estado) {
        Mascota m = new Mascota();
        m.setId(id);
        m.setNombre(nombre);
        m.setEspecie("Perro");
        m.setEdadAproximada("2 años");
        m.setSexo("MACHO");
        m.setDescripcion("Desc");
        m.setEstadoDisponibilidad(estado);
        m.setFechaPublicacion(LocalDateTime.now());
        m.setAdministrador(admin);
        return m;
    }
}
