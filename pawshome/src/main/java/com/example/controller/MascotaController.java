package com.example.controller;

import com.example.model.Mascota;
import com.example.service.MascotaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
