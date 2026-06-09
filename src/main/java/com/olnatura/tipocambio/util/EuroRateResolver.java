package com.olnatura.tipocambio.util;

import com.olnatura.tipocambio.client.BanxicoClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Banxico SF46410 (Euro) no publica sabado/domingo ni algunos festivos.
 * Para esas fechas en Dynamics se usa el ultimo valor publicado (p. ej. el del viernes).
 */
public final class EuroRateResolver {

    private static final int MAX_RETROCESO_DIAS = 14;

    private EuroRateResolver() {
    }

    public static BigDecimal resolverParaFecha(LocalDate fechaObjetivo, Map<LocalDate, BigDecimal> euroPorFecha) {
        return BanxicoSeriesUtils.ultimoValorValido(
                fechaObjetivo, euroPorFecha, MAX_RETROCESO_DIAS, BanxicoClient.SERIE_EURO);
    }
}
