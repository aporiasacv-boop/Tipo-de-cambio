package com.olnatura.tipocambio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParDivisaTest {

    @Test
    void usdUsaSerieDePagosBanxico() {
        assertEquals("USD/MXN", ParDivisa.USD_MXN.etiqueta());
        assertEquals("SF60653", ParDivisa.USD_MXN.serieBanxico());
    }

    @Test
    void euroUsaSerieBanxico() {
        assertEquals("EUR/MXN", ParDivisa.EUR_MXN.etiqueta());
        assertEquals("SF46410", ParDivisa.EUR_MXN.serieBanxico());
    }
}
