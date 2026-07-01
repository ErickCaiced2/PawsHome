package com.example.specification;

import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import org.springframework.data.jpa.domain.Specification;

public class MascotaEspecificacion {

    private MascotaEspecificacion() {}

    public static Specification<Mascota> soloDisponibles() {
        return (root, query, cb) ->
                cb.equal(root.get("estadoDisponibilidad"), EstadoMascota.DISPONIBLE);
    }

    public static Specification<Mascota> conEspecie(String especie) {
        return (root, query, cb) -> (especie == null || especie.isBlank()) ? null
                : cb.equal(cb.lower(root.get("especie")), especie.trim().toLowerCase());
    }

    public static Specification<Mascota> conSexo(String sexo) {
        return (root, query, cb) -> (sexo == null || sexo.isBlank()) ? null
                : cb.equal(root.get("sexo"), sexo.trim());
    }

    public static Specification<Mascota> conNombreContiene(String nombre) {
        return (root, query, cb) -> (nombre == null || nombre.isBlank()) ? null
                : cb.like(cb.lower(root.get("nombre")), "%" + nombre.trim().toLowerCase() + "%");
    }

    public static Specification<Mascota> conRazaContiene(String raza) {
        return (root, query, cb) -> (raza == null || raza.isBlank()) ? null
                : cb.like(cb.lower(root.get("raza")), "%" + raza.trim().toLowerCase() + "%");
    }
}
