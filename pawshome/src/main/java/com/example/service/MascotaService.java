package com.example.service;

import com.example.dto.MascotaForm;
import com.example.exception.AccesoDenegadoException;
import com.example.exception.MascotaNoEncontradaException;
import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import com.example.model.MascotaImagen;
import com.example.model.RolUsuario;
import com.example.model.Usuario;
import com.example.repository.MascotaImagenRepository;
import com.example.repository.MascotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final MascotaImagenRepository mascotaImagenRepository;
    private final BlobStorageService blobStorageService;

    public MascotaService(MascotaRepository mascotaRepository,
                          MascotaImagenRepository mascotaImagenRepository,
                          BlobStorageService blobStorageService) {
        this.mascotaRepository = mascotaRepository;
        this.mascotaImagenRepository = mascotaImagenRepository;
        this.blobStorageService = blobStorageService;
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

    @Transactional(readOnly = true)
    public List<String> listarImagenes(Long mascotaId) {
        return mascotaImagenRepository.findByMascotaIdOrderByOrdenAsc(mascotaId)
                .stream()
                .map(img -> img.getImagenUrl())
                .toList();
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
    public Mascota registrarMascota(MascotaForm form, List<MultipartFile> imagenes, Usuario administrador) {
        if (administrador.getRol() != RolUsuario.ADMINISTRADOR_REFUGIO) {
            throw new AccesoDenegadoException("Solo administradores de refugio pueden registrar mascotas");
        }

        Mascota mascota = new Mascota();
        mascota.setNombre(form.getNombre());
        mascota.setEspecie(form.getEspecie());
        mascota.setRaza(form.getRaza());
        String edadTexto = form.getEdadAproximada().trim();
        double edad = Double.parseDouble(edadTexto.replace(",", "."));
        mascota.setEdadAproximada(edad == 1.0 ? "1 año" : edadTexto + " años");
        mascota.setSexo(form.getSexo());
        mascota.setDescripcion(form.getDescripcion());
        mascota.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
        mascota.setFechaPublicacion(LocalDateTime.now());
        mascota.setAdministrador(administrador);

        for (int i = 0; i < imagenes.size(); i++) {
            try {
                String url = blobStorageService.subir(imagenes.get(i));
                mascota.getImagenes().add(new MascotaImagen(url, i, mascota));
                if (i == 0) {
                    mascota.setImagenUrl(url);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error al subir imagen " + (i + 1) + ": " + e.getMessage(), e);
            }
        }

        return mascotaRepository.save(mascota);
    }
}
