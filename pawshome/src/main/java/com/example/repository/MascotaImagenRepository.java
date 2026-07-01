package com.example.repository;

import com.example.model.MascotaImagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MascotaImagenRepository extends JpaRepository<MascotaImagen, Long> {
    List<MascotaImagen> findByMascotaIdOrderByOrdenAsc(Long mascotaId);
}
