package com.example.service;

import com.example.exception.AccesoDenegadoException;
import com.example.exception.MascotaNoEncontradaException;
import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import com.example.model.RolUsuario;
import com.example.model.Usuario;
import com.example.repository.MascotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;

    public MascotaService(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    @Transactional
    public void cambiarDisponibilidad(Long mascotaId, EstadoMascota nuevoEstado, Usuario administrador) {
        if (administrador.getRol() != RolUsuario.ADMINISTRADOR_REFUGIO) {
            throw new AccesoDenegadoException("Solo administradores de refugio pueden cambiar el estado de una mascota");
        }

        Mascota mascota = mascotaRepository.findByIdAndAdministrador(mascotaId, administrador)
                .orElseThrow(() -> new MascotaNoEncontradaException(
                        "Mascota con id " + mascotaId + " no encontrada o no pertenece al administrador"));

        mascota.setEstadoDisponibilidad(nuevoEstado);
        mascotaRepository.save(mascota);
    }
}
