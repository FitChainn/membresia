package com.fitchain.membresia;

import com.fitchain.membresia.WebClient.ClienteClient;
import com.fitchain.membresia.dto.ClienteDTO;
import com.fitchain.membresia.dto.MembresiaRequestDTO;
import com.fitchain.membresia.dto.MembresiaResponseDTO;
import com.fitchain.membresia.model.EstadoMembresia;
import com.fitchain.membresia.model.Membresia;
import com.fitchain.membresia.model.TipoMembresia;
import com.fitchain.membresia.repository.MembresiaRepository;
import com.fitchain.membresia.service.MembresiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PRUEBAS UNITARIAS DEL SERVICE DE MEMBRESIA")
public class MembresiaServiceTest {

    @Mock
    private MembresiaRepository membresiaRepository;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private MembresiaService membresiaService;

    private Membresia membresia;
    private ClienteDTO clienteDTO;
    private MembresiaRequestDTO mRequest;

    @BeforeEach
    void setUp() {
        membresia = new Membresia(1L, 2L, TipoMembresia.MENSUAL,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1),
                new BigDecimal("25000"), EstadoMembresia.ACTIVA);

        clienteDTO = new ClienteDTO();
        clienteDTO.setId(2L);
        clienteDTO.setNombre("JUANITO PEREZ");
        clienteDTO.setRun("12.123.431-2");
        clienteDTO.setFechaNacimiento(LocalDate.of(1995, 5, 10));

        mRequest = new MembresiaRequestDTO();
        mRequest.setClienteId(2L);
        mRequest.setTipo(TipoMembresia.MENSUAL);
        mRequest.setFechaInicio(LocalDate.of(2026, 1, 1));
        mRequest.setPrecio(new BigDecimal("25000"));
    }

    @Test
    @DisplayName("DEBE CREAR UNA MEMBRESÍA")
    void shouldCrearMembresia() {
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(membresiaRepository.save(any(Membresia.class))).thenReturn(membresia);

        MembresiaResponseDTO result = membresiaService.crear(mRequest);

        assertNotNull(result);
        assertEquals(TipoMembresia.MENSUAL, result.getTipo());
        assertEquals(EstadoMembresia.ACTIVA, result.getEstado());
        assertEquals("JUANITO PEREZ", result.getCliente().getNombre());
        verify(membresiaRepository, times(1)).save(any(Membresia.class));
    }

    @Test
    @DisplayName("DEBE CALCULAR LA FECHA FIN SEGÚN EL TIPO (TRIMESTRAL)")
    void shouldCalcularFechaFinTrimestral() {
        mRequest.setTipo(TipoMembresia.TRIMESTRAL);
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(membresiaRepository.save(any(Membresia.class))).thenReturn(membresia);

        membresiaService.crear(mRequest);

        ArgumentCaptor<Membresia> captor = ArgumentCaptor.forClass(Membresia.class);
        verify(membresiaRepository).save(captor.capture());
        assertEquals(LocalDate.of(2026, 4, 1), captor.getValue().getFechaFin());
    }

    @Test
    @DisplayName("DEBE RETORNAR TODAS LAS MEMBRESÍAS")
    void shouldReturnTodasLasMembresias() {
        Membresia m2 = new Membresia(2L, 3L, TipoMembresia.TRIMESTRAL,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 4, 1),
                new BigDecimal("60000"), EstadoMembresia.ACTIVA);
        ClienteDTO cliente2 = new ClienteDTO();
        cliente2.setId(3L);
        cliente2.setNombre("MARIA GARCIA");

        when(membresiaRepository.findAll()).thenReturn(List.of(membresia, m2));
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(clienteClient.obtenerClientePorId(3L)).thenReturn(cliente2);

        List<MembresiaResponseDTO> result = membresiaService.obtenerTodas();

        assertEquals(2, result.size());
        assertEquals(TipoMembresia.MENSUAL, result.get(0).getTipo());
        assertEquals(TipoMembresia.TRIMESTRAL, result.get(1).getTipo());
        verify(membresiaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("DEBE RETORNAR UNA MEMBRESÍA POR ID")
    void shouldReturnMembresiaById() {
        when(membresiaRepository.findById(1L)).thenReturn(Optional.of(membresia));
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);

        MembresiaResponseDTO result = membresiaService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals(EstadoMembresia.ACTIVA, result.getEstado());
        verify(membresiaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION SI MEMBRESÍA NO EXISTE POR ID")
    void shouldThrowWhenMembresiaNotFoundById() {
        when(membresiaRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> membresiaService.obtenerPorId(99L));

        assertEquals("Membresía con id 99 no encontrada", ex.getMessage());
    }

    @Test
    @DisplayName("DEBE RETORNAR MEMBRESÍAS POR CLIENTE")
    void shouldReturnMembresiasByCliente() {
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(membresiaRepository.findByClienteId(2L)).thenReturn(List.of(membresia));

        List<MembresiaResponseDTO> result = membresiaService.obtenerPorCliente(2L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getCliente().getId());
        verify(membresiaRepository, times(1)).findByClienteId(2L);
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACÍA SI CLIENTE NO TIENE MEMBRESÍAS")
    void shouldReturnEmptyListWhenClienteHasNoMembresias() {
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(membresiaRepository.findByClienteId(2L)).thenReturn(List.of());

        List<MembresiaResponseDTO> result = membresiaService.obtenerPorCliente(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE RETORNAR MEMBRESÍAS POR ESTADO")
    void shouldReturnMembresiasByEstado() {
        when(membresiaRepository.findByEstado(EstadoMembresia.ACTIVA)).thenReturn(List.of(membresia));
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);

        List<MembresiaResponseDTO> result = membresiaService.obtenerPorEstado(EstadoMembresia.ACTIVA);

        assertEquals(1, result.size());
        assertEquals(EstadoMembresia.ACTIVA, result.get(0).getEstado());
        verify(membresiaRepository, times(1)).findByEstado(EstadoMembresia.ACTIVA);
    }

    @Test
    @DisplayName("DEBE ACTUALIZAR UNA MEMBRESÍA")
    void shouldActualizarMembresia() {
        MembresiaRequestDTO updateReq = new MembresiaRequestDTO();
        updateReq.setClienteId(2L);
        updateReq.setTipo(TipoMembresia.TRIMESTRAL);
        updateReq.setFechaInicio(LocalDate.of(2026, 1, 1));
        updateReq.setPrecio(new BigDecimal("60000"));

        Membresia actualizada = new Membresia(1L, 2L, TipoMembresia.TRIMESTRAL,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 4, 1),
                new BigDecimal("60000"), EstadoMembresia.ACTIVA);

        when(membresiaRepository.findById(1L)).thenReturn(Optional.of(membresia));
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(membresiaRepository.save(any(Membresia.class))).thenReturn(actualizada);

        MembresiaResponseDTO result = membresiaService.actualizar(1L, updateReq);

        assertEquals(TipoMembresia.TRIMESTRAL, result.getTipo());
        verify(membresiaRepository, times(1)).findById(1L);
        verify(membresiaRepository, times(1)).save(any(Membresia.class));
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION AL ACTUALIZAR MEMBRESÍA QUE NO EXISTE")
    void shouldThrowWhenActualizarMembresiaNotFound() {
        when(membresiaRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> membresiaService.actualizar(99L, mRequest));

        assertEquals("Membresía con id 99 no encontrada", ex.getMessage());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UNA MEMBRESÍA")
    void shouldEliminarMembresia() {
        when(membresiaRepository.existsById(1L)).thenReturn(true);

        membresiaService.eliminar(1L);

        verify(membresiaRepository, times(1)).existsById(1L);
        verify(membresiaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION AL ELIMINAR MEMBRESÍA QUE NO EXISTE")
    void shouldThrowWhenEliminarMembresiaNotFound() {
        when(membresiaRepository.existsById(99L)).thenReturn(false);

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> membresiaService.eliminar(99L));

        assertEquals("Membresía con id 99 no encontrada", ex.getMessage());
        verify(membresiaRepository, never()).deleteById(99L);
    }
}