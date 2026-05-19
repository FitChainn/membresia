package com.fitchain.membresia.repository;

import com.fitchain.membresia.model.EstadoMembresia;
import com.fitchain.membresia.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findByClienteId(Long clienteId);

    List<Membresia> findByEstado(EstadoMembresia estado);

    List<Membresia> findByClienteIdAndEstado(Long clienteId, EstadoMembresia estado);
}
