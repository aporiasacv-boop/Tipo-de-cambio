package com.olnatura.tipocambio.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixRateResolverTest {

    @Test
    void diaHabilUsaFixMenosDosHabiles() {
        Map<LocalDate, BigDecimal> fix = Map.of(
                LocalDate.of(2026, 5, 26), new BigDecimal("17.3232"),
                LocalDate.of(2026, 5, 27), new BigDecimal("17.3793"));

        BigDecimal tasa = FixRateResolver.resolverParaFecha(LocalDate.of(2026, 5, 28), fix);

        assertEquals(new BigDecimal("17.3232"), tasa);
    }

    @Test
    void neEnFixUsaUltimoValido() {
        Map<LocalDate, BigDecimal> fix = Map.of(
                LocalDate.of(2026, 5, 26), new BigDecimal("17.3232"),
                LocalDate.of(2026, 5, 27), new BigDecimal("17.3793"));

        BigDecimal tasa = FixRateResolver.resolverParaFecha(LocalDate.of(2026, 5, 29), fix);

        assertEquals(new BigDecimal("17.3793"), tasa);
    }

    @Test
    void sabadoUsaMismaTasaQueViernes() {
        Map<LocalDate, BigDecimal> fix = Map.of(
                LocalDate.of(2026, 5, 26), new BigDecimal("17.3232"),
                LocalDate.of(2026, 5, 27), new BigDecimal("17.3793"));

        BigDecimal viernes = FixRateResolver.resolverParaFecha(LocalDate.of(2026, 5, 29), fix);
        BigDecimal sabado = FixRateResolver.resolverParaFecha(LocalDate.of(2026, 5, 30), fix);

        assertEquals(viernes, sabado);
    }
}
