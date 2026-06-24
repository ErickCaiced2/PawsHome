package com.example.controller;

import com.example.dto.MascotaForm;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

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

    @GetMapping("/disponibles")
    public String listarDisponibles(Model model) {
        model.addAttribute("mascotas", mascotaService.listarDisponibles());
        return "mascotas/listado-disponibles";
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
