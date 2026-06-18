package com.fitchain.membresia;

import com.fitchain.membresia.model.EstadoMembresia;
import com.fitchain.membresia.model.Membresia;
import com.fitchain.membresia.model.TipoMembresia;
import com.fitchain.membresia.repository.MembresiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("PRUEBAS UNITARIAS DEL REPOSITORY DE MEMBRESIA")
public class MembresiaRepositoryTest {

    @Autowired
    private MembresiaRepository repo;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void limpiarBDenMemoria() {
        repo.deleteAll();
        em.flush();
    }

    private Membresia crearMembresia(Long clienteId, TipoMembresia tipo, EstadoMembresia estado) {
        Membresia m = new Membresia();
        m.setClienteId(clienteId);
        m.setTipo(tipo);
        m.setFechaInicio(LocalDate.now());
        m.setFechaFin(LocalDate.now().plusMonths(1));
        m.setPrecio(new BigDecimal("25000"));
        m.setEstado(estado);
        return em.persistAndFlush(m);
    }

    @Test
    @DisplayName("DEBE ENCONTRAR UNA MEMBRESÍA POR ID")
    void findById_ShouldReturnMembresia() {
        Membresia m = crearMembresia(1L, TipoMembresia.MENSUAL, EstadoMembresia.ACTIVA);

        Optional<Membresia> result = repo.findById(m.getId());

        assertTrue(result.isPresent());
        assertEquals(TipoMembresia.MENSUAL, result.get().getTipo());
    }

    @Test
    @DisplayName("DEBE RETORNAR VACIO SI MEMBRESÍA NO EXISTE")
    void findById_ShouldReturnEmpty() {
        Optional<Membresia> result = repo.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR TODAS LAS MEMBRESÍAS")
    void findAll_ShouldReturnAllMembresias() {
        crearMembresia(1L, TipoMembresia.MENSUAL, EstadoMembresia.ACTIVA);
        crearMembresia(2L, TipoMembresia.TRIMESTRAL, EstadoMembresia.ACTIVA);

        List<Membresia> lista = repo.findAll();

        assertFalse(lista.isEmpty());
        assertTrue(lista.size() >= 2);
    }

    @Test
    @DisplayName("DEBE GUARDAR UNA MEMBRESÍA")
    void save_ShouldPersistMembresia() {
        Membresia m = new Membresia();
        m.setClienteId(1L);
        m.setTipo(TipoMembresia.ANUAL);
        m.setFechaInicio(LocalDate.now());
        m.setFechaFin(LocalDate.now().plusMonths(12));
        m.setPrecio(new BigDecimal("180000"));
        m.setEstado(EstadoMembresia.ACTIVA);

        Membresia saved = repo.save(m);

        assertNotNull(saved.getId());
        assertEquals(TipoMembresia.ANUAL, saved.getTipo());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR MEMBRESÍAS POR CLIENTE")
    void findByClienteId_ShouldReturnMembresias() {
        crearMembresia(5L, TipoMembresia.MENSUAL, EstadoMembresia.ACTIVA);
        crearMembresia(5L, TipoMembresia.TRIMESTRAL, EstadoMembresia.VENCIDA);

        List<Membresia> result = repo.findByClienteId(5L);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getClienteId().equals(5L)));
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI CLIENTE NO TIENE MEMBRESÍAS")
    void findByClienteId_ShouldReturnEmpty() {
        List<Membresia> result = repo.findByClienteId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR MEMBRESÍAS POR ESTADO")
    void findByEstado_ShouldReturnMembresias() {
        crearMembresia(1L, TipoMembresia.MENSUAL, EstadoMembresia.ACTIVA);
        crearMembresia(2L, TipoMembresia.TRIMESTRAL, EstadoMembresia.ACTIVA);

        List<Membresia> result = repo.findByEstado(EstadoMembresia.ACTIVA);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(m -> m.getEstado() == EstadoMembresia.ACTIVA));
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI NO HAY MEMBRESÍAS CON ESE ESTADO")
    void findByEstado_ShouldReturnEmpty() {
        List<Membresia> result = repo.findByEstado(EstadoMembresia.CANCELADA);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR MEMBRESÍAS POR CLIENTE Y ESTADO")
    void findByClienteIdAndEstado_ShouldReturnMembresias() {
        crearMembresia(7L, TipoMembresia.MENSUAL, EstadoMembresia.ACTIVA);
        crearMembresia(7L, TipoMembresia.TRIMESTRAL, EstadoMembresia.VENCIDA);

        List<Membresia> result = repo.findByClienteIdAndEstado(7L, EstadoMembresia.ACTIVA);

        assertEquals(1, result.size());
        assertEquals(TipoMembresia.MENSUAL, result.get(0).getTipo());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UNA MEMBRESÍA")
    void delete_ShouldRemoveMembresia() {
        Membresia m = crearMembresia(1L, TipoMembresia.MENSUAL, EstadoMembresia.ACTIVA);
        Long id = m.getId();

        repo.deleteById(id);
        em.flush();

        assertFalse(repo.findById(id).isPresent());
    }
}