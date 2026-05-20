package com.fitchain.membresia.controller;

import com.fitchain.membresia.dto.MembresiaRequestDTO;
import com.fitchain.membresia.dto.MembresiaResponseDTO;
import com.fitchain.membresia.model.EstadoMembresia;
import com.fitchain.membresia.service.MembresiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/membresias")
@RequiredArgsConstructor
public class MembresiaController {

    private final MembresiaService membresiaService;

    @PostMapping
    public ResponseEntity<MembresiaResponseDTO> crear(@Valid @RequestBody MembresiaRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membresiaService.crear(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<MembresiaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(membresiaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembresiaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(membresiaService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<MembresiaResponseDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(membresiaService.obtenerPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MembresiaResponseDTO>> obtenerPorEstado(@PathVariable EstadoMembresia estado) {
        return ResponseEntity.ok(membresiaService.obtenerPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MembresiaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MembresiaRequestDTO requestDTO) {
        return ResponseEntity.ok(membresiaService.actualizar(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        membresiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
