package com.olnatura.tipocambio.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExchangeRatesClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generaPayloadDynamicsValidado() throws Exception {
        BigDecimal rate = new BigDecimal("17.2720");
        LocalDate fecha = LocalDate.of(2026, 5, 27);

        var payload = ExchangeRatesClient.buildExchangeRateCreateRequest(rate, fecha);

        String json = objectMapper.writeValueAsString(payload);

        assertEquals("Predeterminado", payload.getRateTypeName());
        assertEquals("USD", payload.getFromCurrency());
        assertEquals("MXN", payload.getToCurrency());
        assertEquals("2026-05-27T12:00:00Z", payload.getStartDate());
        assertEquals(0, rate.compareTo(new BigDecimal(payload.getRate().toString())));
        assertEquals("One", payload.getConversionFactor());
        assertTrue(json.contains("\"FromCurrency\":\"USD\""));
        assertTrue(json.contains("\"StartDate\":\"2026-05-27T12:00:00Z\""));
        assertFalse(json.contains("odata.etag"), "POST no debe enviar @odata.etag");
    }
}
