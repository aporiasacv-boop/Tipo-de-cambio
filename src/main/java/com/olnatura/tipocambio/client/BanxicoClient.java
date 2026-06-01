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
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class BanxicoClient {

    public static final String SERIE_PAGOS = "SF60653";

    private static final int DIAS_MAX_UNA_CONSULTA = 400;

    private static final DateTimeFormatter BANXICO_PATH_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final RestClient restClient;
    private final BanxicoProperties banxicoProperties;

    public Map<LocalDate, BigDecimal> obtenerMapaPagos(LocalDate desde, LocalDate hasta) {
        return obtenerMapaSerie(SERIE_PAGOS, desde, hasta);
    }

    public Map<LocalDate, BigDecimal> obtenerMapaSerie(String serieId, LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("desde no puede ser posterior a hasta");
        }
        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;
        if (dias <= DIAS_MAX_UNA_CONSULTA) {
            return consultarSerie(serieId, desde, hasta);
        }
        Map<LocalDate, BigDecimal> acumulado = new TreeMap<>();
        LocalDate tramoDesde = desde;
        while (!tramoDesde.isAfter(hasta)) {
            LocalDate finAnio = LocalDate.of(tramoDesde.getYear(), 12, 31);
            LocalDate tramoHasta = finAnio.isBefore(hasta) ? finAnio : hasta;
            acumulado.putAll(consultarSerie(serieId, tramoDesde, tramoHasta));
            tramoDesde = tramoHasta.plusDays(1);
        }
        return acumulado;
    }

    private Map<LocalDate, BigDecimal> consultarSerie(String serieId, LocalDate desde, LocalDate hasta) {
        String url = String.format(
                "https://www.banxico.org.mx/SieAPIRest/service/v1/series/%s/datos/%s/%s",
                serieId,
                desde.format(BANXICO_PATH_DATE),
                hasta.format(BANXICO_PATH_DATE));

        BanxicoResponse response = restClient.get()
                .uri(url)
                .header("Bmx-Token", banxicoProperties.getToken())
                .header("Accept", "application/json")
                .retrieve()
                .body(BanxicoResponse.class);

        return parsearMapaSerie(response);
    }

    static Map<LocalDate, BigDecimal> parsearMapaSerie(BanxicoResponse response) {
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

    /** @deprecated usar {@link #parsearMapaSerie} */
    @Deprecated
    static Map<LocalDate, BigDecimal> parsearMapaFix(BanxicoResponse response) {
        return parsearMapaSerie(response);
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
            throw new IllegalStateException("Valor Banxico invalido: " + valor, e);
        }
    }
}
