package com.fitchain.membresia.dto;

import com.fitchain.membresia.model.EstadoMembresia;
import com.fitchain.membresia.model.TipoMembresia;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MembresiaResponseDTO {

    private Long id;
    private TipoMembresia tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal precio;
    private EstadoMembresia estado;

    // Datos del cliente traídos vía WebClient
    private ClienteDTO cliente;
}
