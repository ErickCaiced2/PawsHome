package com.example.repository;

import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import com.example.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long>, JpaSpecificationExecutor<Mascota> {
    Optional<Mascota> findByIdAndAdministrador(Long id, Usuario administrador);
    List<Mascota> findByEstadoDisponibilidad(EstadoMascota estado);
    List<Mascota> findByAdministradorId(Long administradorId);
    Optional<Mascota> findByIdAndAdministradorId(Long id, Long administradorId);
}
