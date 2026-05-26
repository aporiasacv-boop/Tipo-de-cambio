package com.olnatura.tipocambio.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olnatura.tipocambio.model.banxico.BanxicoResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BanxicoClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void extraeTipoCambioDesdeRespuestaBanxico() throws Exception {
        String json = """
                {
                  "bmx": {
                    "series": [
                      {
                        "datos": [
                          {
                            "fecha": "27/05/2026",
                            "dato": "17.2720"
                          }
                        ]
                      }
                    ]
                  }
                }
                """;

        BanxicoResponse response = objectMapper.readValue(json, BanxicoResponse.class);

        BigDecimal rate = BanxicoClient.extraerTipoCambio(response);

        assertEquals(new BigDecimal("17.2720"), rate);
    }

    @Test
    void fallaSiRespuestaSinDatos() {
        BanxicoResponse response = new BanxicoResponse();
        BanxicoResponse.Bmx bmx = new BanxicoResponse.Bmx();
        response.setBmx(bmx);

        assertThrows(IllegalStateException.class, () -> BanxicoClient.extraerTipoCambio(response));
    }
}
