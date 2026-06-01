package com.olnatura.tipocambio.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PagosRateResolverTest {

    @Test
    void usaPagosDelMismoDia() {
        Map<LocalDate, BigDecimal> pagos = Map.of(
                LocalDate.of(2026, 5, 28), new BigDecimal("17.3232"),
                LocalDate.of(2026, 5, 29), new BigDecimal("17.3793"));

        BigDecimal tasa = PagosRateResolver.resolverParaFecha(LocalDate.of(2026, 5, 29), pagos);

        assertEquals(new BigDecimal("17.3793"), tasa);
    }

    @Test
    void sinPagosEnFechaRetrocedeAlUltimoValido() {
        Map<LocalDate, BigDecimal> pagos = Map.of(
                LocalDate.of(2026, 5, 28), new BigDecimal("17.3232"));

        BigDecimal sabado = PagosRateResolver.resolverParaFecha(LocalDate.of(2026, 5, 30), pagos);

        assertEquals(new BigDecimal("17.3232"), sabado);
    }
}
