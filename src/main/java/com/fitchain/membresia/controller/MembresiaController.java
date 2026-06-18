package com.fitchain.membresia.controller;

import com.fitchain.membresia.assembler.MembresiaModelAssembler;
import com.fitchain.membresia.dto.MembresiaRequestDTO;
import com.fitchain.membresia.dto.MembresiaResponseDTO;
import com.fitchain.membresia.model.EstadoMembresia;
import com.fitchain.membresia.service.MembresiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "MEMBRESÍAS", description = "GESTIÓN DE MEMBRESÍAS")
@RestController
@RequestMapping("/v1/membresias")
@RequiredArgsConstructor
public class MembresiaController {

    private final MembresiaService membresiaService;
    private final MembresiaModelAssembler assembler;

    @Operation(summary = "CREAR MEMBRESÍA", description = "Crea una nueva membresía. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Membresía creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "503", description = "Microservicio no disponible")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EntityModel<MembresiaResponseDTO>> crear(@Valid @RequestBody MembresiaRequestDTO requestDTO) {
        MembresiaResponseDTO creada = membresiaService.crear(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creada));
    }

    @Operation(summary = "OBTENER TODAS LAS MEMBRESÍAS", description = "Retorna la lista de todas las membresías. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MembresiaResponseDTO>>> obtenerTodas() {
        List<EntityModel<MembresiaResponseDTO>> membresias = membresiaService.obtenerTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(membresias,
                linkTo(methodOn(MembresiaController.class).obtenerTodas()).withSelfRel()));
    }

    @Operation(summary = "OBTENER MEMBRESÍA POR ID", description = "Retorna una membresía específica por su ID. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membresía encontrada"),
            @ApiResponse(responseCode = "404", description = "Membresía no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MembresiaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(membresiaService.obtenerPorId(id)));
    }

    @Operation(summary = "OBTENER MEMBRESÍAS POR CLIENTE", description = "Retorna todas las membresías de un cliente. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CollectionModel<EntityModel<MembresiaResponseDTO>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<EntityModel<MembresiaResponseDTO>> membresias = membresiaService.obtenerPorCliente(clienteId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(membresias,
                linkTo(methodOn(MembresiaController.class).obtenerPorCliente(clienteId)).withSelfRel()));
    }

    @Operation(summary = "OBTENER MEMBRESÍAS POR ESTADO", description = "Retorna membresías filtradas por estado (ACTIVA, VENCIDA, CANCELADA). Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<CollectionModel<EntityModel<MembresiaResponseDTO>>> obtenerPorEstado(@PathVariable EstadoMembresia estado) {
        List<EntityModel<MembresiaResponseDTO>> membresias = membresiaService.obtenerPorEstado(estado).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(membresias,
                linkTo(methodOn(MembresiaController.class).obtenerPorEstado(estado)).withSelfRel()));
    }

    @Operation(summary = "ACTUALIZAR MEMBRESÍA", description = "Actualiza una membresía existente. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membresía actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Membresía no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<MembresiaResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MembresiaRequestDTO requestDTO) {
        return ResponseEntity.ok(assembler.toModel(membresiaService.actualizar(id, requestDTO)));
    }

    @Operation(summary = "ELIMINAR MEMBRESÍA", description = "Elimina una membresía por su ID. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Membresía eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Membresía no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        membresiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}