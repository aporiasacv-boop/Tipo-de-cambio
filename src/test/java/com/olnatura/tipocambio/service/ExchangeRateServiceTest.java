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
    void inicioHistorialUsdMxnEs29Feb2020() {
        assertEquals(LocalDate.of(2020, 2, 29), ExchangeRateService.FECHA_INICIO_HISTORIAL);
    }

    @Test
    void detectaHuecosConRegistrosPosteriores() {
        Set<LocalDate> existentes = Set.of(
                LocalDate.of(2026, 5, 26),
                LocalDate.of(2026, 5, 27),
                LocalDate.of(2026, 5, 28),
                LocalDate.of(2026, 5, 29),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 2));

        List<LocalDate> faltantes = ExchangeRateService.listarFechasFaltantes(
                existentes,
                LocalDate.of(2026, 5, 26),
                LocalDate.of(2026, 6, 2));

        assertEquals(
                List.of(
                        LocalDate.of(2026, 5, 30),
                        LocalDate.of(2026, 5, 31)),
                faltantes);
    }

    @Test
    void detectaHuecosHastaRegistroPosterior() {
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
