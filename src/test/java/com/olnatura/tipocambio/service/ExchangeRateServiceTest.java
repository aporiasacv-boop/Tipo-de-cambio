package com.olnatura.tipocambio.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExchangeRateServiceTest {

    @Test
    void retrocesoOperacionSon14Dias() {
        assertEquals(14, ExchangeRateService.DIAS_RETROCESO_OPERACION);
    }

    @Test
    void fechaDesdeSinRegistrosRetrocede14Dias() {
        LocalDate hoy = LocalDate.of(2026, 6, 5);
        LocalDate desde = ExchangeRateService.calcularFechaDesde(hoy, Set.of(), LocalDate.of(2026, 6, 10));
        assertEquals(LocalDate.of(2026, 5, 22), desde);
    }

    @Test
    void fechaDesdeContinuaDesdeUltimoRegistro() {
        LocalDate hoy = LocalDate.of(2026, 6, 5);
        Set<LocalDate> existentes = Set.of(LocalDate.of(2026, 6, 1));
        LocalDate desde = ExchangeRateService.calcularFechaDesde(hoy, existentes, LocalDate.of(2026, 6, 10));
        assertEquals(LocalDate.of(2026, 6, 2), desde);
    }

    @Test
    void fechaDesdeNoRetrocedeMasDe14Dias() {
        LocalDate hoy = LocalDate.of(2026, 6, 5);
        Set<LocalDate> existentes = Set.of(LocalDate.of(2026, 5, 1));
        LocalDate desde = ExchangeRateService.calcularFechaDesde(hoy, existentes, LocalDate.of(2026, 6, 10));
        assertEquals(LocalDate.of(2026, 5, 22), desde);
    }

    @Test
    void listarFechasFaltantesDetectaHuecos() {
        Set<LocalDate> existentes = Set.of(
                LocalDate.of(2026, 5, 29),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 2));

        List<LocalDate> faltantes = ExchangeRateService.listarFechasFaltantes(
                existentes,
                LocalDate.of(2026, 5, 29),
                LocalDate.of(2026, 6, 2));

        assertEquals(
                List.of(
                        LocalDate.of(2026, 5, 30),
                        LocalDate.of(2026, 5, 31)),
                faltantes);
    }

    @Test
    void ultimaFechaPublicadaEsElMaximoDelMapaBanxico() {
        Map<LocalDate, BigDecimal> pagos = Map.of(
                LocalDate.of(2026, 5, 29), new BigDecimal("17.37"),
                LocalDate.of(2026, 6, 1), new BigDecimal("17.32"));

        assertEquals(LocalDate.of(2026, 6, 1), ExchangeRateService.ultimaFechaPublicada(pagos));
    }

    @Test
    void sinHuecosDevuelveListaVacia() {
        Set<LocalDate> existentes = Set.of(
                LocalDate.of(2026, 5, 29),
                LocalDate.of(2026, 5, 30),
                LocalDate.of(2026, 5, 31));

        List<LocalDate> faltantes = ExchangeRateService.listarFechasFaltantes(
                existentes,
                LocalDate.of(2026, 5, 29),
                LocalDate.of(2026, 5, 31));

        assertTrue(faltantes.isEmpty());
    }
}
