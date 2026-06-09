package com.olnatura.tipocambio.service;

import com.olnatura.tipocambio.model.ParDivisa;
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
    void listarFechasFaltantesSoloConDatoBanxico() {
        Set<LocalDate> existentes = Set.of(
                LocalDate.of(2026, 5, 29),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 2));
        Map<LocalDate, BigDecimal> banxico = Map.of(
                LocalDate.of(2026, 5, 29), new BigDecimal("17.37"),
                LocalDate.of(2026, 5, 30), new BigDecimal("17.38"),
                LocalDate.of(2026, 6, 1), new BigDecimal("17.32"),
                LocalDate.of(2026, 6, 2), new BigDecimal("17.33"));

        List<LocalDate> faltantes = ExchangeRateService.listarFechasFaltantesConDatoBanxico(
                existentes,
                banxico,
                LocalDate.of(2026, 5, 29),
                LocalDate.of(2026, 6, 2));

        assertEquals(List.of(LocalDate.of(2026, 5, 30)), faltantes);
    }

    @Test
    void listarFechasFaltantesNoInventaFinesDeSemanaEuro() {
        Set<LocalDate> existentes = Set.of(LocalDate.of(2026, 5, 29));
        Map<LocalDate, BigDecimal> banxico = Map.of(
                LocalDate.of(2026, 5, 29), new BigDecimal("20.20"),
                LocalDate.of(2026, 6, 1), new BigDecimal("20.24"));

        List<LocalDate> faltantes = ExchangeRateService.listarFechasFaltantesConDatoBanxico(
                existentes,
                banxico,
                LocalDate.of(2026, 5, 29),
                LocalDate.of(2026, 6, 1));

        assertEquals(List.of(LocalDate.of(2026, 6, 1)), faltantes);
    }

    @Test
    void listarFechasFaltantesEncuentra9JunSiBanxicoLoPublico() {
        Set<LocalDate> existentes = Set.of(
                LocalDate.of(2026, 6, 3),
                LocalDate.of(2026, 6, 8));
        Map<LocalDate, BigDecimal> banxico = Map.of(
                LocalDate.of(2026, 6, 9), new BigDecimal("17.47"));

        List<LocalDate> faltantes = ExchangeRateService.listarFechasFaltantesConDatoBanxico(
                existentes,
                banxico,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 9));

        assertTrue(faltantes.contains(LocalDate.of(2026, 6, 9)));
    }

    @Test
    void usdFechaFinIncluyeMananaPublicadaEnBanxico() {
        LocalDate hoy = LocalDate.of(2026, 6, 8);
        LocalDate ultimaBanxico = LocalDate.of(2026, 6, 9);
        assertEquals(ultimaBanxico, ParDivisa.USD_MXN.calcularFechaFin(hoy, ultimaBanxico));
    }

    @Test
    void ultimaFechaPublicadaEsElMaximoDelMapaBanxico() {
        Map<LocalDate, BigDecimal> pagos = Map.of(
                LocalDate.of(2026, 5, 29), new BigDecimal("17.37"),
                LocalDate.of(2026, 6, 9), new BigDecimal("17.47"));

        assertEquals(LocalDate.of(2026, 6, 9), ExchangeRateService.ultimaFechaPublicada(pagos));
    }

    @Test
    void sinHuecosDevuelveListaVacia() {
        Set<LocalDate> existentes = Set.of(
                LocalDate.of(2026, 5, 29),
                LocalDate.of(2026, 5, 30),
                LocalDate.of(2026, 5, 31));

        Map<LocalDate, BigDecimal> banxico = Map.of(
                LocalDate.of(2026, 5, 29), new BigDecimal("17.37"),
                LocalDate.of(2026, 5, 30), new BigDecimal("17.38"),
                LocalDate.of(2026, 5, 31), new BigDecimal("17.39"));
        List<LocalDate> faltantes = ExchangeRateService.listarFechasFaltantesConDatoBanxico(
                existentes,
                banxico,
                LocalDate.of(2026, 5, 29),
                LocalDate.of(2026, 5, 31));

        assertTrue(faltantes.isEmpty());
    }
}
