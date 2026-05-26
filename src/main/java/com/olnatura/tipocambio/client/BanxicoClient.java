package com.olnatura.tipocambio.client;

import com.olnatura.tipocambio.config.BanxicoProperties;
import com.olnatura.tipocambio.model.banxico.BanxicoResponse;
import com.olnatura.tipocambio.model.banxico.Dato;
import com.olnatura.tipocambio.model.banxico.Series;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BanxicoClient {

    private static final String BANXICO_URL =
            "https://www.banxico.org.mx/SieAPIRest/service/v1/series/SF60653/datos/oportuno";

    private final RestClient restClient;
    private final BanxicoProperties banxicoProperties;

    public BigDecimal obtenerTipoCambioParaPagos() {
        BanxicoResponse response = restClient.get()
                .uri(BANXICO_URL)
                .header("Bmx-Token", banxicoProperties.getToken())
                .header("Accept", "application/json")
                .retrieve()
                .body(BanxicoResponse.class);

        return extraerTipoCambio(response);
    }

    static BigDecimal extraerTipoCambio(BanxicoResponse response) {
        if (response == null || response.getBmx() == null) {
            throw new IllegalStateException("Respuesta Banxico sin estructura bmx");
        }
        List<Series> series = response.getBmx().getSeries();
        if (series == null || series.isEmpty()) {
            throw new IllegalStateException("Respuesta Banxico sin series");
        }
        List<Dato> datos = series.get(0).getDatos();
        if (datos == null || datos.isEmpty()) {
            throw new IllegalStateException("Respuesta Banxico sin datos");
        }
        String valor = datos.get(0).getDato();
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Valor de tipo de cambio Banxico vacío");
        }
        try {
            return new BigDecimal(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Valor de tipo de cambio Banxico inválido: " + valor, e);
        }
    }
}
