package com.example.controller;

import com.example.dto.MascotaForm;
import com.example.model.Mascota;
import com.example.model.RolUsuario;
import com.example.model.Usuario;
import com.example.repository.UsuarioRepository;
import com.example.service.MascotaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class MascotaControllerTest {

    private MockMvc mockMvc;
    private Usuario admin;
    private CapturingMascotaService mascotaService;

    @BeforeEach
    void setUp() {
        admin = new Usuario();
        admin.setCorreo("admin@example.com");
        admin.setRol(RolUsuario.ADMINISTRADOR_REFUGIO);

        mascotaService = new CapturingMascotaService();
        MascotaController controller = new MascotaController(mascotaService, usuarioRepository(admin));

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @Test
    void mostrarFormularioRegistro_agregaMascotaFormAlModelo() throws Exception {
        mockMvc.perform(get("/mascotas/nueva"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro-mascota"))
                .andExpect(model().attributeExists("mascotaForm"));
    }

    @Test
    void registrarMascota_valida_redirigeAlInicioYRegistra() throws Exception {
        mockMvc.perform(post("/mascotas")
                        .principal(() -> "admin@example.com")
                        .param("nombre", "Luna")
                        .param("especie", "Perro")
                        .param("raza", "Mestiza")
                        .param("edadAproximada", "2 años")
                        .param("sexo", "Hembra")
                        .param("descripcion", "Tranquila y cariñosa")
                        .param("imagenUrl", "https://example.com/luna.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertNotNull(mascotaService.form.get());
        assertSame(admin, mascotaService.administrador.get());
    }

    @Test
    void registrarMascota_invalida_vuelveAlFormularioConErrores() throws Exception {
        mockMvc.perform(post("/mascotas")
                        .principal(() -> "admin@example.com")
                        .param("nombre", "")
                        .param("especie", "")
                        .param("edadAproximada", "")
                        .param("sexo", "")
                        .param("descripcion", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("registro-mascota"))
                .andExpect(model().attributeHasFieldErrors(
                        "mascotaForm", "nombre", "especie", "edadAproximada", "sexo", "descripcion"));
    }

    private UsuarioRepository usuarioRepository(Usuario usuario) {
        return (UsuarioRepository) Proxy.newProxyInstance(
                UsuarioRepository.class.getClassLoader(),
                new Class<?>[]{UsuarioRepository.class},
                (proxy, method, args) -> {
                    if ("findByCorreo".equals(method.getName())) {
                        return Optional.of(usuario);
                    }
                    if ("toString".equals(method.getName())) {
                        return "TestUsuarioRepository";
                    }
                    throw new UnsupportedOperationException("Metodo no usado en esta prueba: " + method.getName());
                });
    }

    private static class CapturingMascotaService extends MascotaService {
        private final AtomicReference<MascotaForm> form = new AtomicReference<>();
        private final AtomicReference<Usuario> administrador = new AtomicReference<>();

        CapturingMascotaService() {
            super(null);
        }

        @Override
        public Mascota registrarMascota(MascotaForm form, Usuario administrador) {
            this.form.set(form);
            this.administrador.set(administrador);
            return new Mascota();
        }
    }
}
