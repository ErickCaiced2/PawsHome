package com.example.controller;

import com.example.dto.MascotaFiltroDTO;
import com.example.model.EstadoMascota;
import com.example.model.Mascota;
import com.example.service.MascotaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class CatalogoControllerTest {

    private MockMvc mockMvc;
    private MascotaService mascotaService;

    @BeforeEach
    void setUp() {
        mascotaService = mock(MascotaService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CatalogoController(mascotaService))
                .build();
    }

    @Test
    void catalogo_sinFiltros_retornaVistaConMascotas() throws Exception {
        Mascota m = new Mascota();
        m.setEstadoDisponibilidad(EstadoMascota.DISPONIBLE);
        when(mascotaService.listarConFiltros(any(MascotaFiltroDTO.class))).thenReturn(List.of(m));

        mockMvc.perform(get("/catalogo"))
                .andExpect(status().isOk())
                .andExpect(view().name("mascotas/listado-disponibles"))
                .andExpect(model().attributeExists("mascotas"))
                .andExpect(model().attributeExists("filtro"));
    }

    @Test
    void catalogo_conFiltros_delegaAlServicio() throws Exception {
        when(mascotaService.listarConFiltros(any(MascotaFiltroDTO.class))).thenReturn(List.of());

        mockMvc.perform(get("/catalogo")
                        .param("especie", "Perro")
                        .param("sexo", "Macho")
                        .param("nombre", "luna")
                        .param("raza", "labrador"))
                .andExpect(status().isOk())
                .andExpect(view().name("mascotas/listado-disponibles"));

        verify(mascotaService).listarConFiltros(any(MascotaFiltroDTO.class));
    }

    @Test
    void catalogo_sinResultados_retornaListaVacia() throws Exception {
        when(mascotaService.listarConFiltros(any(MascotaFiltroDTO.class))).thenReturn(List.of());

        mockMvc.perform(get("/catalogo").param("especie", "Conejo"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("mascotas", List.of()));
    }
}
