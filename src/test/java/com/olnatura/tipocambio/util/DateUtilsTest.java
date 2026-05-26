package com.olnatura.tipocambio.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateUtilsTest {

    @Test
    void parseaFechaBanxico() {
        LocalDate date = DateUtils.parseBanxicoDate("27/05/2026");
        assertEquals(LocalDate.of(2026, 5, 27), date);
    }

    @Test
    void comparaSoloFechaDynamics() {
        LocalDate hoy = LocalDate.of(2026, 5, 27);
        LocalDate desdeDynamics = DateUtils.parseDynamicsStartDate("2026-05-27T12:00:00Z");
        assertTrue(DateUtils.isSameDate(hoy, desdeDynamics));
    }
}
