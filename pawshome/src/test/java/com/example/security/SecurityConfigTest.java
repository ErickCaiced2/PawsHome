package com.example.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rutaLogin_sinAutenticacion_esAccesible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void rutaNuevaMascota_sinAutenticacion_redirigALogin() throws Exception {
        mockMvc.perform(get("/mascotas/nueva"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void rutaGestion_sinAutenticacion_redirigALogin() throws Exception {
        mockMvc.perform(get("/mascotas/gestion"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void rutaDisponibilidad_sinAutenticacion_redirigALogin() throws Exception {
        mockMvc.perform(get("/mascotas/1/disponibilidad"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_REFUGIO")
    void rutaNuevaMascota_conAdminRefugio_noRedirigALogin() throws Exception {
        mockMvc.perform(get("/mascotas/nueva"))
                .andExpect(result -> {
                    String location = result.getResponse().getHeader("Location");
                    assertTrue(location == null || !location.contains("login"),
                            "No debe redirigir al login con rol ADMINISTRADOR_REFUGIO");
                });
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void rutaNuevaMascota_conRolUsuario_retornaAccesoDenegado() throws Exception {
        // accessDeniedPage hace forward interno, el status sigue siendo 403
        mockMvc.perform(get("/mascotas/nueva"))
                .andExpect(status().isForbidden());
    }
}
