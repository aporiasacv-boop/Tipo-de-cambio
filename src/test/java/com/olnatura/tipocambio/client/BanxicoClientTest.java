package com.olnatura.tipocambio.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olnatura.tipocambio.model.banxico.BanxicoResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BanxicoClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parseaMapaFixIgnorandoNe() throws Exception {
        String json = """
                {
                  "bmx": {
                    "series": [
                      {
                        "datos": [
                          { "fecha": "26/05/2026", "dato": "17.3232" },
                          { "fecha": "27/05/2026", "dato": "17.3793" },
                          { "fecha": "28/05/2026", "dato": "N/E" }
                        ]
                      }
                    ]
                  }
                }
                """;

        BanxicoResponse response = objectMapper.readValue(json, BanxicoResponse.class);
        Map<LocalDate, BigDecimal> mapa = BanxicoClient.parsearMapaFix(response);

        assertEquals(2, mapa.size());
        assertEquals(new BigDecimal("17.3232"), mapa.get(LocalDate.of(2026, 5, 26)));
        assertEquals(new BigDecimal("17.3793"), mapa.get(LocalDate.of(2026, 5, 27)));
        assertFalse(mapa.containsKey(LocalDate.of(2026, 5, 28)));
    }

    @Test
    void detectaNe() {
        assertTrue(BanxicoClient.esNoDisponible("N/E"));
        assertTrue(BanxicoClient.esNoDisponible(" n/e "));
        assertFalse(BanxicoClient.esNoDisponible("17.2720"));
    }

    @Test
    void fallaSiRespuestaSinSeries() {
        BanxicoResponse response = new BanxicoResponse();
        BanxicoResponse.Bmx bmx = new BanxicoResponse.Bmx();
        response.setBmx(bmx);

        assertThrows(IllegalStateException.class, () -> BanxicoClient.parsearMapaFix(response));
    }
}
