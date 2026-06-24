package com.example.service;

import com.example.dto.MascotaForm;
import com.example.exception.AccesoDenegadoException;
import com.example.exception.MascotaNoEncontradaException;
import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import com.example.model.RolUsuario;
import com.example.model.Usuario;
import com.example.repository.MascotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private MascotaService mascotaService;

    @Test
    void cambiarDisponibilidad_conRolYMascotaCorrectos_actualizaEstado() {
        Usuario admin = new Usuario();
        admin.setRol(RolUsuario.ADMINISTRADOR_REFUGIO);

        Mascota mascota = new Mascota();
        mascota.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);

        when(mascotaRepository.findByIdAndAdministrador(1L, admin)).thenReturn(Optional.of(mascota));
        when(mascotaRepository.save(mascota)).thenReturn(mascota);

        mascotaService.cambiarDisponibilidad(1L, EstadoMascota.ADOPTADO, admin);

        assertEquals(EstadoMascota.ADOPTADO, mascota.getEstadoDisponibilidad());
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void cambiarDisponibilidad_mascotaNoExiste_lanzaMascotaNoEncontradaException() {
        Usuario admin = new Usuario();
        admin.setRol(RolUsuario.ADMINISTRADOR_REFUGIO);

        when(mascotaRepository.findByIdAndAdministrador(99L, admin)).thenReturn(Optional.empty());

        assertThrows(MascotaNoEncontradaException.class,
                () -> mascotaService.cambiarDisponibilidad(99L, EstadoMascota.ADOPTADO, admin));

        verify(mascotaRepository, never()).save(any());
    }

    @Test
    void cambiarDisponibilidad_mascotaAjena_lanzaMascotaNoEncontradaException() {
        Usuario admin = new Usuario();
        admin.setRol(RolUsuario.ADMINISTRADOR_REFUGIO);

        when(mascotaRepository.findByIdAndAdministrador(5L, admin)).thenReturn(Optional.empty());

        assertThrows(MascotaNoEncontradaException.class,
                () -> mascotaService.cambiarDisponibilidad(5L, EstadoMascota.EN_PROCESO, admin));

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

    @Test
    void registrarMascota_conAdminRefugio_guardaMascotaDisponible() {
        Usuario admin = new Usuario();
        admin.setRol(RolUsuario.ADMINISTRADOR_REFUGIO);

        MascotaForm form = new MascotaForm(
                "Luna",
                "Perro",
                "Mestiza",
                "2 años",
                "Hembra",
                "Tranquila y cariñosa",
                "https://example.com/luna.jpg"
        );

        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota mascota = mascotaService.registrarMascota(form, admin);

        assertEquals("Luna", mascota.getNombre());
        assertEquals("Perro", mascota.getEspecie());
        assertEquals("Mestiza", mascota.getRaza());
        assertEquals("2 años", mascota.getEdadAproximada());
        assertEquals("Hembra", mascota.getSexo());
        assertEquals("Tranquila y cariñosa", mascota.getDescripcion());
        assertEquals("https://example.com/luna.jpg", mascota.getImagenUrl());
        assertEquals(EstadoMascota.DISPONIBLE, mascota.getEstadoDisponibilidad());
        assertSame(admin, mascota.getAdministrador());
        assertNotNull(mascota.getFechaPublicacion());
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void registrarMascota_conRolIncorrecto_lanzaAccesoDenegadoException() {
        Usuario usuario = new Usuario();
        usuario.setRol(RolUsuario.USUARIO);
        MascotaForm form = new MascotaForm();

        assertThrows(AccesoDenegadoException.class,
                () -> mascotaService.registrarMascota(form, usuario));

        verify(mascotaRepository, never()).save(any());
    }
}
