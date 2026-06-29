package com.example.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mascotas")
@NoArgsConstructor
@AllArgsConstructor
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 80, nullable = false)
    private String nombre;

    @Column(name = "especie", length = 50, nullable = false)
    private String especie;

    @Column(name = "raza", length = 80)
    private String raza;

    @Column(name = "edad_aproximada", length = 50, nullable = false)
    private String edadAproximada;

    @Column(name = "sexo", length = 20, nullable = false)
    private String sexo;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_disponibilidad", length = 30, nullable = false)
    private EstadoMascota estadoDisponibilidad = EstadoMascota.DISPONIBLE;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime fechaPublicacion = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_id", nullable = false)
    private Usuario administrador;
}
