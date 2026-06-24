package com.example.controller;

import com.example.dto.MascotaForm;
import com.example.exception.MascotaNoEncontradaException;
import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import com.example.model.Usuario;
import com.example.repository.UsuarioRepository;
import com.example.service.MascotaService;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private static final String FORM_ATTRIBUTE = "mascotaForm";
    private static final String REGISTRO_VIEW = "registro-mascota";

    private final MascotaService mascotaService;
    private final UsuarioRepository usuarioRepository;

    public MascotaController(MascotaService mascotaService, UsuarioRepository usuarioRepository) {
        this.mascotaService = mascotaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/{id}/disponibilidad")
    public String mostrarCambioDisponibilidad(@PathVariable Long id,
                                              @RequestParam(required = false) EstadoMascota nuevoEstado,
                                              Model model,
                                              Principal principal) {
        Usuario admin = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + principal.getName()));
        Mascota mascota = mascotaService.findByIdAndAdministrador(id, admin.getId())
                .orElseThrow(() -> new MascotaNoEncontradaException(
                        "Mascota " + id + " no encontrada o no pertenece al administrador"));
        model.addAttribute("mascota", mascota);
        model.addAttribute("estados", EstadoMascota.values());
        model.addAttribute("nuevoEstado", nuevoEstado);
        model.addAttribute("administradorId", admin.getId());
        return "mascotas/cambiar-disponibilidad";
    }

    @PostMapping("/{id}/disponibilidad")
    public String cambiarDisponibilidad(@PathVariable Long id,
                                        @RequestParam EstadoMascota nuevoEstado,
                                        Principal principal) {
        Usuario admin = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + principal.getName()));
        mascotaService.cambiarDisponibilidad(id, nuevoEstado, admin);
        return "redirect:/mascotas/gestion?administradorId=" + admin.getId();
    }

    @GetMapping("/disponibles")
    public String listarDisponibles(Model model) {
        model.addAttribute("mascotas", mascotaService.listarDisponibles());
        return "mascotas/listado-disponibles";
    }

    @GetMapping("/gestion")
    public String gestionAdministrador(@RequestParam("administradorId") Long administradorId, Model model) {
        List<Mascota> mascotas = mascotaService.listarPorAdministrador(administradorId);
        model.addAttribute("mascotas", mascotas);
        return "mascotas/gestion";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioRegistro(Model model) {
        if (!model.containsAttribute(FORM_ATTRIBUTE)) {
            model.addAttribute(FORM_ATTRIBUTE, new MascotaForm());
        }
        return REGISTRO_VIEW;
    }

    @PostMapping
    public String registrarMascota(@Valid @ModelAttribute(FORM_ATTRIBUTE) MascotaForm mascotaForm,
                                   BindingResult bindingResult,
                                   Principal principal) {
        if (bindingResult.hasErrors()) {
            return REGISTRO_VIEW;
        }

        Usuario administrador = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + principal.getName()));

        mascotaService.registrarMascota(mascotaForm, administrador);
        return "redirect:/";
    }
}
