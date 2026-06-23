package com.example.service;

import com.example.dto.MascotaForm;
import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import com.example.model.Usuario;
import com.example.repository.MascotaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;

    public MascotaService(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    /**
     * TASK 1.4 — Registrar una mascota disponible asociada al administrador autenticado.
     * Asigna EstadoMascota.DISPONIBLE y fechaPublicacion = ahora antes de persistir.
     */
    public Mascota registrarMascota(MascotaForm form, Usuario administrador) {
        Mascota mascota = new Mascota();
        mascota.setNombre(form.getNombre());
        mascota.setEspecie(form.getEspecie());
        mascota.setRaza(form.getRaza());
        mascota.setEdadAproximada(form.getEdadAproximada());
        mascota.setSexo(form.getSexo());
        mascota.setDescripcion(form.getDescripcion());
        mascota.setImagenUrl(form.getImagenUrl());
        mascota.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
        mascota.setFechaPublicacion(LocalDateTime.now());
        mascota.setAdministrador(administrador);
        return mascotaRepository.save(mascota);
    }

    /** TASK 2.1 — Consultar solo mascotas DISPONIBLE para el catálogo público. */
    public List<Mascota> listarDisponibles() {
        return mascotaRepository.findByEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
    }

    public List<Mascota> findByAdministrador(Long administradorId) {
        return mascotaRepository.findByAdministradorId(administradorId);
    }

    public Optional<Mascota> findByIdAndAdministrador(Long id, Long administradorId) {
        return mascotaRepository.findByIdAndAdministradorId(id, administradorId);
    }
}
