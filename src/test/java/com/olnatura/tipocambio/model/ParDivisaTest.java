package com.olnatura.tipocambio.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParDivisaTest {

    @Test
    void usdHastaUltimaFechaBanxico() {
        LocalDate ultimaBanxico = LocalDate.of(2026, 6, 10);
        assertEquals(ultimaBanxico, ParDivisa.USD_MXN.calcularFechaFin(
                LocalDate.of(2026, 6, 9), ultimaBanxico));
    }

    @Test
    void euroNoInsertaHoySiBanxicoSoloTieneAyer() {
        LocalDate hoy = LocalDate.of(2026, 6, 9);
        LocalDate ultimaBanxico = LocalDate.of(2026, 6, 8);
        assertEquals(ultimaBanxico, ParDivisa.EUR_MXN.calcularFechaFin(hoy, ultimaBanxico));
    }

    @Test
    void euroHastaUltimaPublicada() {
        LocalDate ultimaBanxico = LocalDate.of(2026, 6, 8);
        assertEquals(ultimaBanxico, ParDivisa.EUR_MXN.calcularFechaFin(
                LocalDate.of(2026, 6, 8), ultimaBanxico));
    }
}
