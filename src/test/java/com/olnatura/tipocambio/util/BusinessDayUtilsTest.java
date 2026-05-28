package com.olnatura.tipocambio.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessDayUtilsTest {

    @Test
    void sumaDosDiasHabilesDesdeJueves() {
        LocalDate resultado = BusinessDayUtils.sumarDiasHabiles(LocalDate.of(2026, 5, 28), 2);
        assertEquals(LocalDate.of(2026, 6, 1), resultado);
    }

    @Test
    void restaDosDiasHabiles() {
        LocalDate resultado = BusinessDayUtils.restarDiasHabiles(LocalDate.of(2026, 5, 29), 2);
        assertEquals(LocalDate.of(2026, 5, 27), resultado);
    }

    @Test
    void ultimoViernesDesdeSabado() {
        LocalDate viernes = BusinessDayUtils.ultimoViernes(LocalDate.of(2026, 5, 30));
        assertEquals(LocalDate.of(2026, 5, 29), viernes);
    }
}
