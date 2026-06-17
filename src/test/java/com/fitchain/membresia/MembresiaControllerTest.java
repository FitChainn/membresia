package com.fitchain.membresia;

import com.fitchain.membresia.config.SecurityConfig;
import com.fitchain.membresia.controller.MembresiaController;
import com.fitchain.membresia.dto.MembresiaRequestDTO;
import com.fitchain.membresia.dto.MembresiaResponseDTO;
import com.fitchain.membresia.filter.RolHeaderFilter;
import com.fitchain.membresia.model.TipoMembresia;
import com.fitchain.membresia.model.EstadoMembresia;
import com.fitchain.membresia.service.MembresiaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MembresiaController.class)
@Import({SecurityConfig.class, RolHeaderFilter.class})
public class MembresiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MembresiaService membresiaService;

    @Autowired
    private ObjectMapper objectMapper;

    private MembresiaResponseDTO mResponse;
    private MembresiaRequestDTO mRequest;

    @BeforeEach
    void setUp() {
        mResponse = new MembresiaResponseDTO();
        mResponse.setId(1L);
        mResponse.setTipo(TipoMembresia.MENSUAL);
        mResponse.setEstado(EstadoMembresia.ACTIVA);
        mResponse.setPrecio(new BigDecimal("30000"));
        mResponse.setFechaInicio(LocalDate.of(2025, 1, 1));

        mRequest = new MembresiaRequestDTO();
        mRequest.setClienteId(1L);
        mRequest.setTipo(TipoMembresia.MENSUAL);
        mRequest.setFechaInicio(LocalDate.of(2025, 1, 1));
        mRequest.setPrecio(new BigDecimal("30000"));
    }

    @Test
    void Post_crear201() throws Exception {
        when(membresiaService.crear(any(MembresiaRequestDTO.class))).thenReturn(mResponse);

        mockMvc.perform(post("/v1/membresias")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void Get_obtenerTodas() throws Exception {
        when(membresiaService.obtenerTodas()).thenReturn(List.of(mResponse));

        mockMvc.perform(get("/v1/membresias")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void Get_obtenerPorId() throws Exception {
        when(membresiaService.obtenerPorId(1L)).thenReturn(mResponse);

        mockMvc.perform(get("/v1/membresias/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void Put_actualizar() throws Exception {
        when(membresiaService.actualizar(eq(1L), any(MembresiaRequestDTO.class))).thenReturn(mResponse);

        mockMvc.perform(put("/v1/membresias/1")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void Delete_eliminar() throws Exception {
        mockMvc.perform(delete("/v1/membresias/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());
    }
}
