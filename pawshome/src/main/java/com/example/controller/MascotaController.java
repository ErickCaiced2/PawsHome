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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private static final String FORM_ATTRIBUTE = "mascotaForm";
    private static final String REGISTRO_VIEW = "registro-mascota";
    private static final Set<String> TIPOS_IMAGEN_PERMITIDOS =
            Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_BYTES_POR_IMAGEN = 5L * 1024 * 1024;

    private final MascotaService mascotaService;
    private final UsuarioRepository usuarioRepository;

    public MascotaController(MascotaService mascotaService, UsuarioRepository usuarioRepository) {
        this.mascotaService = mascotaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/{id}/imagenes")
    @ResponseBody
    public List<String> listarImagenes(@PathVariable Long id) {
        return mascotaService.listarImagenes(id);
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
        return "mascotas/cambiar-disponibilidad";
    }

    @PostMapping("/{id}/disponibilidad")
    public String cambiarDisponibilidad(@PathVariable Long id,
                                        @RequestParam EstadoMascota nuevoEstado,
                                        Principal principal) {
        Usuario admin = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + principal.getName()));
        mascotaService.cambiarDisponibilidad(id, nuevoEstado, admin);
        return "redirect:/mascotas/gestion";
    }

    @GetMapping("/disponibles")
    public String listarDisponibles(Model model) {
        model.addAttribute("mascotas", mascotaService.listarDisponibles());
        return "mascotas/listado-disponibles";
    }

    @GetMapping("/gestion")
    public String gestionAdministrador(Model model, Principal principal) {
        Usuario admin = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + principal.getName()));
        model.addAttribute("mascotas", mascotaService.listarPorAdministrador(admin.getId()));
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
                                   @RequestParam(name = "imagenes", required = false) List<MultipartFile> imagenes,
                                   Model model,
                                   Principal principal) {

        String imagenError = validarImagenes(imagenes);

        if (bindingResult.hasErrors() || imagenError != null) {
            if (imagenError != null) {
                model.addAttribute("imagenError", imagenError);
            }
            return REGISTRO_VIEW;
        }

        List<MultipartFile> archivos = imagenes.stream()
                .filter(f -> !f.isEmpty())
                .collect(Collectors.toList());

        Usuario administrador = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + principal.getName()));

        try {
            mascotaService.registrarMascota(mascotaForm, archivos, administrador);
        } catch (RuntimeException e) {
            model.addAttribute("imagenError", "Error al subir imágenes: " + e.getMessage());
            return REGISTRO_VIEW;
        }

        return "redirect:/mascotas/gestion";
    }

    private String validarImagenes(List<MultipartFile> imagenes) {
        List<MultipartFile> validas = imagenes == null ? List.of() :
                imagenes.stream().filter(f -> f != null && !f.isEmpty()).collect(Collectors.toList());

        if (validas.size() < 3) {
            return "Debes subir al menos 3 imágenes";
        }
        for (MultipartFile archivo : validas) {
            if (!TIPOS_IMAGEN_PERMITIDOS.contains(archivo.getContentType())) {
                return "Solo se permiten imágenes JPG, PNG o WEBP (archivo inválido: " + archivo.getOriginalFilename() + ")";
            }
            if (archivo.getSize() > MAX_BYTES_POR_IMAGEN) {
                return "Cada imagen debe pesar menos de 5 MB (archivo muy grande: " + archivo.getOriginalFilename() + ")";
            }
        }
        return null;
    }
}
