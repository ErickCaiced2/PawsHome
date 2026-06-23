package com.example.repository;

import com.example.model.Mascota;
import com.example.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    Optional<Mascota> findByIdAndAdministrador(Long id, Usuario administrador);
}
