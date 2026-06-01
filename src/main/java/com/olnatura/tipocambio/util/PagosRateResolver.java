package com.olnatura.tipocambio.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Tasa para pagos (serie SF60653): valor del mismo día o el último publicado hacia atrás.
 */
public final class PagosRateResolver {

    private static final int MAX_RETROCESO_DIAS = 90;

    private PagosRateResolver() {
    }

    public static BigDecimal resolverParaFecha(LocalDate fechaObjetivo, Map<LocalDate, BigDecimal> pagosPorFecha) {
        return BanxicoSeriesUtils.ultimoValorValido(
                fechaObjetivo, pagosPorFecha, MAX_RETROCESO_DIAS, "para pagos (SF60653)");
    }
}
