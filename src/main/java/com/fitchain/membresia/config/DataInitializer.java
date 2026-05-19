package com.fitchain.membresia.config;

import com.fitchain.membresia.model.EstadoMembresia;
import com.fitchain.membresia.model.Membresia;
import com.fitchain.membresia.model.TipoMembresia;
import com.fitchain.membresia.repository.MembresiaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MembresiaRepository membresiaRepository;

    @Override
    public void run(String... args) {
        if (membresiaRepository.count() == 0) {
            log.info("Cargando datos de prueba para Membresía...");

            LocalDate hoy = LocalDate.now();

            Membresia m1 = new Membresia();
            m1.setClienteId(1L);
            m1.setTipo(TipoMembresia.MENSUAL);
            m1.setFechaInicio(hoy);
            m1.setFechaFin(hoy.plusMonths(1));
            m1.setPrecio(new BigDecimal("25000"));
            m1.setEstado(EstadoMembresia.ACTIVA);

            Membresia m2 = new Membresia();
            m2.setClienteId(2L);
            m2.setTipo(TipoMembresia.TRIMESTRAL);
            m2.setFechaInicio(hoy.minusMonths(1));
            m2.setFechaFin(hoy.minusMonths(1).plusMonths(3));
            m2.setPrecio(new BigDecimal("60000"));
            m2.setEstado(EstadoMembresia.ACTIVA);

            Membresia m3 = new Membresia();
            m3.setClienteId(3L);
            m3.setTipo(TipoMembresia.ANUAL);
            m3.setFechaInicio(hoy.minusMonths(13));
            m3.setFechaFin(hoy.minusMonths(13).plusMonths(12));
            m3.setPrecio(new BigDecimal("180000"));
            m3.setEstado(EstadoMembresia.VENCIDA);

            Membresia m4 = new Membresia();
            m4.setClienteId(1L);
            m4.setTipo(TipoMembresia.MENSUAL);
            m4.setFechaInicio(hoy.minusMonths(2));
            m4.setFechaFin(hoy.minusMonths(1));
            m4.setPrecio(new BigDecimal("25000"));
            m4.setEstado(EstadoMembresia.CANCELADA);

            membresiaRepository.saveAll(List.of(m1, m2, m3, m4));
            log.info("Datos de prueba cargados: {} membresías", membresiaRepository.count());
        } else {
            log.info("Ya existen datos en la base de datos, omitiendo inicialización");
        }
    }
}
