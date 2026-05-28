package com.olnatura.tipocambio.client;

import com.olnatura.tipocambio.config.BanxicoProperties;
import com.olnatura.tipocambio.model.banxico.BanxicoResponse;
import com.olnatura.tipocambio.model.banxico.Dato;
import com.olnatura.tipocambio.model.banxico.Series;
import com.olnatura.tipocambio.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BanxicoClient {

    private static final String SERIE_FIX = "SF43718";
    private static final DateTimeFormatter BANXICO_PATH_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final RestClient restClient;
    private final BanxicoProperties banxicoProperties;

    public Map<LocalDate, BigDecimal> obtenerMapaFix(LocalDate desde, LocalDate hasta) {
        String url = String.format(
                "https://www.banxico.org.mx/SieAPIRest/service/v1/series/%s/datos/%s/%s",
                SERIE_FIX,
                desde.format(BANXICO_PATH_DATE),
                hasta.format(BANXICO_PATH_DATE));

        BanxicoResponse response = restClient.get()
                .uri(url)
                .header("Bmx-Token", banxicoProperties.getToken())
                .header("Accept", "application/json")
                .retrieve()
                .body(BanxicoResponse.class);

        return parsearMapaFix(response);
    }

    static Map<LocalDate, BigDecimal> parsearMapaFix(BanxicoResponse response) {
        if (response == null || response.getBmx() == null) {
            throw new IllegalStateException("Respuesta Banxico sin estructura bmx");
        }
        List<Series> series = response.getBmx().getSeries();
        if (series == null || series.isEmpty()) {
            throw new IllegalStateException("Respuesta Banxico sin series");
        }
        List<Dato> datos = series.get(0).getDatos();
        if (datos == null || datos.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<LocalDate, BigDecimal> mapa = new HashMap<>();
        for (Dato dato : datos) {
            if (dato.getFecha() == null || esNoDisponible(dato.getDato())) {
                continue;
            }
            LocalDate fecha = DateUtils.parseBanxicoDate(dato.getFecha());
            mapa.put(fecha, parsearValor(dato.getDato()));
        }
        return mapa;
    }

    static boolean esNoDisponible(String valor) {
        if (valor == null || valor.isBlank()) {
            return true;
        }
        return "N/E".equalsIgnoreCase(valor.trim());
    }

    static BigDecimal parsearValor(String valor) {
        try {
            return new BigDecimal(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Valor FIX Banxico invalido: " + valor, e);
        }
    }
}
