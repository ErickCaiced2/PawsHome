package com.example.service;

import com.example.dto.MascotaForm;
import com.example.exception.AccesoDenegadoException;
import com.example.exception.MascotaNoEncontradaException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
                3.0, "Macho", "Muy amigable", "http://img.com/bobby.jpg"
        );

        administrador = new Usuario();
        administrador.setId(1L);
        administrador.setNombre("Admin Test");
        administrador.setCorreo("admin@pawshome.com");
        administrador.setPasswordHash("hash");
        administrador.setRol(RolUsuario.ADMINISTRADOR_REFUGIO);
        administrador.setActivo(true);
    }

    // ── cambiarDisponibilidad ─────────────────────────────────────────────────

    @Test
    void cambiarDisponibilidad_conRolYMascotaCorrectos_actualizaEstado() {
        Mascota mascota = new Mascota();
        mascota.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);

        when(mascotaRepository.findByIdAndAdministrador(1L, administrador)).thenReturn(Optional.of(mascota));
        when(mascotaRepository.save(mascota)).thenReturn(mascota);

        mascotaService.cambiarDisponibilidad(1L, EstadoMascota.ADOPTADO, administrador);

        assertEquals(EstadoMascota.ADOPTADO, mascota.getEstadoDisponibilidad());
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void cambiarDisponibilidad_mascotaNoExiste_lanzaMascotaNoEncontradaException() {
        when(mascotaRepository.findByIdAndAdministrador(99L, administrador)).thenReturn(Optional.empty());

        assertThrows(MascotaNoEncontradaException.class,
                () -> mascotaService.cambiarDisponibilidad(99L, EstadoMascota.ADOPTADO, administrador));

        verify(mascotaRepository, never()).save(any());
    }

    @Test
    void cambiarDisponibilidad_mascotaAjena_lanzaMascotaNoEncontradaException() {
        when(mascotaRepository.findByIdAndAdministrador(5L, administrador)).thenReturn(Optional.empty());

        assertThrows(MascotaNoEncontradaException.class,
                () -> mascotaService.cambiarDisponibilidad(5L, EstadoMascota.EN_PROCESO, administrador));

        verify(mascotaRepository, never()).save(any());
    }

    @Test
    void cambiarDisponibilidad_rolIncorrecto_lanzaAccesoDenegadoException() {
        Usuario usuario = new Usuario();
        usuario.setRol(RolUsuario.USUARIO);

        assertThrows(AccesoDenegadoException.class,
                () -> mascotaService.cambiarDisponibilidad(1L, EstadoMascota.ADOPTADO, usuario));

        verify(mascotaRepository, never()).findByIdAndAdministrador(any(), any());
        verify(mascotaRepository, never()).save(any());
    }

    // ── registrarMascota ──────────────────────────────────────────────────────

    @Test
    void registrarMascota_conAdminRefugio_guardaMascotaDisponible() {
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota mascota = mascotaService.registrarMascota(formValido, administrador);

        assertEquals("Bobby", mascota.getNombre());
        assertEquals(EstadoMascota.DISPONIBLE, mascota.getEstadoDisponibilidad());
        assertSame(administrador, mascota.getAdministrador());
        assertNotNull(mascota.getFechaPublicacion());
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void registrarMascota_debeAsignarEstadoDisponible() {
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));

        Mascota resultado = mascotaService.registrarMascota(formValido, administrador);

        assertThat(resultado.getEstadoDisponibilidad()).isEqualTo(EstadoMascota.DISPONIBLE);
    }

    @Test
    void registrarMascota_debePersistirConFechaPublicacionActual() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        ArgumentCaptor<Mascota> captor = ArgumentCaptor.forClass(Mascota.class);
        when(mascotaRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        mascotaService.registrarMascota(formValido, administrador);

        LocalDateTime fechaAsignada = captor.getValue().getFechaPublicacion();
        assertThat(fechaAsignada).isAfterOrEqualTo(antes);
        assertThat(fechaAsignada).isBeforeOrEqualTo(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void registrarMascota_conRolIncorrecto_lanzaAccesoDenegadoException() {
        Usuario usuario = new Usuario();
        usuario.setRol(RolUsuario.USUARIO);

        assertThrows(AccesoDenegadoException.class,
                () -> mascotaService.registrarMascota(new MascotaForm(), usuario));

        verify(mascotaRepository, never()).save(any());
    }

    // ── listarDisponibles ─────────────────────────────────────────────────────

    @Test
    void listarDisponibles_soloRetornaMascotasDisponibles() {
        Mascota m1 = new Mascota(); m1.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
        Mascota m2 = new Mascota(); m2.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
        when(mascotaRepository.findByEstadoDisponibilidad(EstadoMascota.DISPONIBLE))
                .thenReturn(List.of(m1, m2));

        List<Mascota> resultado = mascotaService.listarDisponibles();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(m -> m.getEstadoDisponibilidad() == EstadoMascota.DISPONIBLE);
        verify(mascotaRepository).findByEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
    }

    @Test
    void listarDisponibles_cuandoNoHayMascotas_retornaListaVacia() {
        when(mascotaRepository.findByEstadoDisponibilidad(EstadoMascota.DISPONIBLE))
                .thenReturn(List.of());

        List<Mascota> resultado = mascotaService.listarDisponibles();

        assertThat(resultado).isEmpty();
    }

    // ── listarPorAdministrador ────────────────────────────────────────────────

    @Test
    void listarPorAdministrador_retornaSoloMascotasDeEseAdmin() {
        Long adminId = 10L;
        Mascota m1 = new Mascota(); m1.setAdministrador(administrador);
        Mascota m2 = new Mascota(); m2.setAdministrador(administrador);
        when(mascotaRepository.findByAdministradorId(adminId)).thenReturn(List.of(m1, m2));

        List<Mascota> resultado = mascotaService.listarPorAdministrador(adminId);

        assertThat(resultado).hasSize(2);
        verify(mascotaRepository).findByAdministradorId(adminId);
    }

    @Test
    void listarPorAdministrador_delegaFiltradoAlRepositorio() {
        Long adminId = 99L;
        when(mascotaRepository.findByAdministradorId(adminId)).thenReturn(List.of());

        mascotaService.listarPorAdministrador(adminId);

        verify(mascotaRepository, times(1)).findByAdministradorId(adminId);
        verify(mascotaRepository, never()).findAll();
    }

    // ── estados del enum ──────────────────────────────────────────────────────

    @ParameterizedTest(name = "EstadoMascota.{0} existe en el enum")
    @EnumSource(value = EstadoMascota.class, names = {"DISPONIBLE", "EN_PROCESO", "ADOPTADO"})
    void estadoMascota_losEstadosRequeridosExistenEnElEnum(EstadoMascota estado) {
        assertThat(estado).isNotNull();
    }
}
