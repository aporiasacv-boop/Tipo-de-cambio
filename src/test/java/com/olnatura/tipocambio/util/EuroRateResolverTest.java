package com.olnatura.tipocambio.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EuroRateResolverTest {

    @Test
    void viernesSePropagaASabadoYDomingo() {
        Map<LocalDate, BigDecimal> euro = Map.of(
                LocalDate.of(2026, 5, 28), new BigDecimal("20.1022"),
                LocalDate.of(2026, 5, 29), new BigDecimal("20.2012"),
                LocalDate.of(2026, 6, 1), new BigDecimal("20.2402"));

        BigDecimal sabado = EuroRateResolver.resolverParaFecha(LocalDate.of(2026, 5, 30), euro);
        BigDecimal domingo = EuroRateResolver.resolverParaFecha(LocalDate.of(2026, 5, 31), euro);

        assertEquals(new BigDecimal("20.2012"), sabado);
        assertEquals(new BigDecimal("20.2012"), domingo);
    }

    @Test
    void lunesUsaValorPublicado() {
        Map<LocalDate, BigDecimal> euro = Map.of(
                LocalDate.of(2026, 5, 29), new BigDecimal("20.2012"),
                LocalDate.of(2026, 6, 1), new BigDecimal("20.2402"));

        assertEquals(new BigDecimal("20.2402"),
                EuroRateResolver.resolverParaFecha(LocalDate.of(2026, 6, 1), euro));
    }
}
