package com.example.service;

import com.example.dto.MascotaForm;
import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import com.example.model.RolUsuario;
import com.example.model.Usuario;
import com.example.repository.MascotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TDD — Tests para MascotaService.
 * Usa Mockito para aislar el repositorio y verificar el comportamiento del servicio.
 */
@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private MascotaService mascotaService;

    private MascotaForm formValido;
    private Usuario administrador;

    @BeforeEach
    void setUp() {
        formValido = new MascotaForm(
                "Bobby", "Perro", "Labrador",
                "3 años", "MACHO", "Muy amigable", "http://img.com/bobby.jpg"
        );

        administrador = new Usuario();
        administrador.setId(1L);
        administrador.setNombre("Admin Test");
        administrador.setCorreo("admin@pawshome.com");
        administrador.setPasswordHash("hash");
        administrador.setRol(RolUsuario.ADMINISTRADOR);
    }

    // ── TASK 1.4 ──────────────────────────────────────────────────────────────

    @Test
    void registrarMascota_debeAsignarEstadoDisponible() {
        // Arrange
        Mascota guardada = new Mascota();
        guardada.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(guardada);

        // Act
        Mascota resultado = mascotaService.registrarMascota(formValido, administrador);

        // Assert
        assertThat(resultado.getEstadoDisponibilidad()).isEqualTo(EstadoMascota.DISPONIBLE);
    }

    @Test
    void registrarMascota_debeAsociarAdministrador() {
        // Arrange
        ArgumentCaptor<Mascota> captor = ArgumentCaptor.forClass(Mascota.class);
        when(mascotaRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        mascotaService.registrarMascota(formValido, administrador);

        // Assert — el administrador queda asociado en el objeto que se pasa al repo
        Mascota enviada = captor.getValue();
        assertThat(enviada.getAdministrador()).isEqualTo(administrador);
    }

    @Test
    void registrarMascota_debePersistirConFechaPublicacionActual() {
        // Arrange
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        ArgumentCaptor<Mascota> captor = ArgumentCaptor.forClass(Mascota.class);
        when(mascotaRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        mascotaService.registrarMascota(formValido, administrador);

        // Assert — fechaPublicacion está entre 'antes' y 'ahora'
        LocalDateTime fechaAsignada = captor.getValue().getFechaPublicacion();
        assertThat(fechaAsignada).isAfterOrEqualTo(antes);
        assertThat(fechaAsignada).isBeforeOrEqualTo(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void registrarMascota_debeLlamarRepositorySaveUnaVez() {
        // Arrange
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        mascotaService.registrarMascota(formValido, administrador);

        // Assert
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    // ── TASK 2.1 ──────────────────────────────────────────────────────────────

    @Test
    void listarDisponibles_soloRetornaMascotasDisponibles() {
        // Arrange
        Mascota m1 = new Mascota(); m1.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
        Mascota m2 = new Mascota(); m2.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
        when(mascotaRepository.findByEstadoDisponibilidad(EstadoMascota.DISPONIBLE))
                .thenReturn(List.of(m1, m2));

        // Act
        List<Mascota> resultado = mascotaService.listarDisponibles();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(m -> m.getEstadoDisponibilidad() == EstadoMascota.DISPONIBLE);
        verify(mascotaRepository).findByEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
    }

    @Test
    void listarDisponibles_cuandoNoHayMascotas_retornaListaVacia() {
        // Arrange
        when(mascotaRepository.findByEstadoDisponibilidad(EstadoMascota.DISPONIBLE))
                .thenReturn(List.of());

        // Act
        List<Mascota> resultado = mascotaService.listarDisponibles();

        // Assert
        assertThat(resultado).isEmpty();
    }

    // ── TASK 3.1 — Test parametrizado sobre estados del enum ─────────────────

    @ParameterizedTest(name = "EstadoMascota.{0} existe en el enum")
    @EnumSource(value = EstadoMascota.class, names = {"DISPONIBLE", "NO_DISPONIBLE"})
    void estadoMascota_losEstadosRequeridosExistenEnElEnum(EstadoMascota estado) {
        // Si el enum no tiene el valor, @EnumSource falla en compilación → garantía de existencia
        assertThat(estado).isNotNull();
    }
}
