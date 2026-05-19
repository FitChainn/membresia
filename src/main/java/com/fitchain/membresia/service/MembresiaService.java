package com.fitchain.membresia.service;

import com.fitchain.membresia.WebClient.ClienteClient;
import com.fitchain.membresia.dto.ClienteDTO;
import com.fitchain.membresia.dto.MembresiaRequestDTO;
import com.fitchain.membresia.dto.MembresiaResponseDTO;
import com.fitchain.membresia.model.EstadoMembresia;
import com.fitchain.membresia.model.Membresia;
import com.fitchain.membresia.model.TipoMembresia;
import com.fitchain.membresia.repository.MembresiaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final ClienteClient clienteClient;

    // ─── Calcular fechaFin según tipo ─────────────────────────────────────────

    private LocalDate calcularFechaFin(LocalDate fechaInicio, TipoMembresia tipo) {
        return switch (tipo) {
            case MENSUAL     -> fechaInicio.plusMonths(1);
            case TRIMESTRAL  -> fechaInicio.plusMonths(3);
            case ANUAL       -> fechaInicio.plusMonths(12);
        };
    }

    // ─── Mapear entidad → ResponseDTO ─────────────────────────────────────────

    private MembresiaResponseDTO toResponseDTO(Membresia membresia, ClienteDTO cliente) {
        MembresiaResponseDTO dto = new MembresiaResponseDTO();
        dto.setId(membresia.getId());
        dto.setTipo(membresia.getTipo());
        dto.setFechaInicio(membresia.getFechaInicio());
        dto.setFechaFin(membresia.getFechaFin());
        dto.setPrecio(membresia.getPrecio());
        dto.setEstado(membresia.getEstado());
        dto.setCliente(cliente);
        return dto;
    }

    // ─── CRUD ──────────────────────────────────────────────────────────────────

    public MembresiaResponseDTO crear(MembresiaRequestDTO requestDTO) {
        log.info("Creando membresía para clienteId {}", requestDTO.getClienteId());

        ClienteDTO cliente = clienteClient.obtenerClientePorId(requestDTO.getClienteId());

        Membresia membresia = new Membresia();
        membresia.setClienteId(requestDTO.getClienteId());
        membresia.setTipo(requestDTO.getTipo());
        membresia.setFechaInicio(requestDTO.getFechaInicio());
        membresia.setFechaFin(calcularFechaFin(requestDTO.getFechaInicio(), requestDTO.getTipo()));
        membresia.setPrecio(requestDTO.getPrecio());
        membresia.setEstado(EstadoMembresia.ACTIVA);

        Membresia guardada = membresiaRepository.save(membresia);
        log.info("Membresía creada con id {}", guardada.getId());
        return toResponseDTO(guardada, cliente);
    }

    public List<MembresiaResponseDTO> obtenerTodas() {
        log.info("Obteniendo todas las membresías");
        return membresiaRepository.findAll().stream()
                .map(m -> toResponseDTO(m, clienteClient.obtenerClientePorId(m.getClienteId())))
                .toList();
    }

    public MembresiaResponseDTO obtenerPorId(Long id) {
        log.info("Buscando membresía con id {}", id);
        Membresia membresia = membresiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Membresía con id " + id + " no encontrada"));
        return toResponseDTO(membresia, clienteClient.obtenerClientePorId(membresia.getClienteId()));
    }

    public List<MembresiaResponseDTO> obtenerPorCliente(Long clienteId) {
        log.info("Buscando membresías del cliente {}", clienteId);
        ClienteDTO cliente = clienteClient.obtenerClientePorId(clienteId);
        return membresiaRepository.findByClienteId(clienteId).stream()
                .map(m -> toResponseDTO(m, cliente))
                .toList();
    }

    public List<MembresiaResponseDTO> obtenerPorEstado(EstadoMembresia estado) {
        log.info("Buscando membresías con estado {}", estado);
        return membresiaRepository.findByEstado(estado).stream()
                .map(m -> toResponseDTO(m, clienteClient.obtenerClientePorId(m.getClienteId())))
                .toList();
    }

    public MembresiaResponseDTO actualizar(Long id, MembresiaRequestDTO requestDTO) {
        log.info("Actualizando membresía con id {}", id);
        Membresia membresia = membresiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Membresía con id " + id + " no encontrada"));

        ClienteDTO cliente = clienteClient.obtenerClientePorId(requestDTO.getClienteId());

        membresia.setClienteId(requestDTO.getClienteId());
        membresia.setTipo(requestDTO.getTipo());
        membresia.setFechaInicio(requestDTO.getFechaInicio());
        membresia.setFechaFin(calcularFechaFin(requestDTO.getFechaInicio(), requestDTO.getTipo()));
        membresia.setPrecio(requestDTO.getPrecio());

        Membresia actualizada = membresiaRepository.save(membresia);
        log.info("Membresía {} actualizada correctamente", id);
        return toResponseDTO(actualizada, cliente);
    }

    public MembresiaResponseDTO cancelar(Long id) {
        log.info("Cancelando membresía con id {}", id);
        Membresia membresia = membresiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Membresía con id " + id + " no encontrada"));

        membresia.setEstado(EstadoMembresia.CANCELADA);
        Membresia cancelada = membresiaRepository.save(membresia);
        log.info("Membresía {} cancelada", id);
        return toResponseDTO(cancelada, clienteClient.obtenerClientePorId(cancelada.getClienteId()));
    }

    public void eliminar(Long id) {
        log.info("Eliminando membresía con id {}", id);
        if (!membresiaRepository.existsById(id)) {
            throw new NoSuchElementException("Membresía con id " + id + " no encontrada");
        }
        membresiaRepository.deleteById(id);
        log.info("Membresía {} eliminada", id);
    }
}
