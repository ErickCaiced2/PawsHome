package com.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mascota_imagenes")
public class MascotaImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imagen_url", length = 500, nullable = false)
    private String imagenUrl;

    @Column(name = "orden", nullable = false)
    private int orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    public MascotaImagen(String imagenUrl, int orden, Mascota mascota) {
        this.imagenUrl = imagenUrl;
        this.orden = orden;
        this.mascota = mascota;
    }
}
