package com.example.service;

import com.example.dto.MascotaForm;
import com.example.exception.AccesoDenegadoException;
import com.example.exception.MascotaNoEncontradaException;
import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import com.example.model.RolUsuario;
import com.example.model.Usuario;
import com.example.repository.MascotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;

    public MascotaService(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    @Transactional(readOnly = true)
    public List<Mascota> listarDisponibles() {
        return mascotaRepository.findByEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
    }

    @Transactional(readOnly = true)
    public List<Mascota> listarPorAdministrador(Long administradorId) {
        return mascotaRepository.findByAdministradorId(administradorId);
    }

    @Transactional(readOnly = true)
    public Optional<Mascota> findByIdAndAdministrador(Long id, Long administradorId) {
        return mascotaRepository.findByIdAndAdministradorId(id, administradorId);
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

    @Transactional
    public Mascota registrarMascota(MascotaForm form, Usuario administrador) {
        if (administrador.getRol() != RolUsuario.ADMINISTRADOR_REFUGIO) {
            throw new AccesoDenegadoException("Solo administradores de refugio pueden registrar mascotas");
        }

        Mascota mascota = new Mascota();
        mascota.setNombre(form.getNombre());
        mascota.setEspecie(form.getEspecie());
        mascota.setRaza(form.getRaza());
        double edad = form.getEdadAproximada();
        mascota.setEdadAproximada(edad == Math.floor(edad)
                ? (int) edad + " años"
                : edad + " años");
        mascota.setSexo(form.getSexo());
        mascota.setDescripcion(form.getDescripcion());
        mascota.setImagenUrl(form.getImagenUrl());
        mascota.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
        mascota.setFechaPublicacion(LocalDateTime.now());
        mascota.setAdministrador(administrador);

        return mascotaRepository.save(mascota);
    }
}
