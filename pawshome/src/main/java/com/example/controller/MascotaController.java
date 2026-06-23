package com.example.controller;

import com.example.model.Mascota;
import com.example.service.MascotaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * TASK 2.1 — Ruta pública de catálogo básico.
 * GET /mascotas/disponibles devuelve solo mascotas con estado DISPONIBLE.
 */
@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping("/disponibles")
    public String catalogoDisponibles(Model model) {
        List<Mascota> mascotas = mascotaService.listarDisponibles();
        model.addAttribute("mascotas", mascotas);
        return "mascotas/disponibles";
    }

    /**
     * TASK 3.4 - Listado de gestion del administrador.
     *
     * TODO: cuando exista autenticacion, obtener el id del administrador desde
     * el usuario autenticado y proteger esta ruta para ADMINISTRADOR_REFUGIO.
     */
    @GetMapping("/gestion")
    public String gestionAdministrador(@RequestParam("administradorId") Long administradorId, Model model) {
        List<Mascota> mascotas = mascotaService.listarPorAdministrador(administradorId);
        model.addAttribute("mascotas", mascotas);
        return "mascotas/gestion";
    }
}
