package com.example.controller;

import com.example.dto.MascotaFiltroDTO;
import com.example.service.MascotaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    private final MascotaService mascotaService;

    public CatalogoController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping("/{id}")
    public String perfil(@PathVariable Long id, Model model) {
        return mascotaService.buscarPorId(id)
                .map(mascota -> {
                    model.addAttribute("mascota", mascota);
                    model.addAttribute("imagenes", mascotaService.listarImagenes(id));
                    return "mascotas/perfil-mascota";
                })
                .orElse("redirect:/catalogo");
    }

    @GetMapping
    public String catalogo(@RequestParam(required = false) String nombre,
                           @RequestParam(required = false) String especie,
                           @RequestParam(required = false) String raza,
                           @RequestParam(required = false) String sexo,
                           Model model) {
        MascotaFiltroDTO filtro = new MascotaFiltroDTO(nombre, especie, raza, sexo);
        model.addAttribute("mascotas", mascotaService.listarConFiltros(filtro));
        model.addAttribute("filtro", filtro);
        return "mascotas/listado-disponibles";
    }
}
