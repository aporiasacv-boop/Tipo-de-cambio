package com.olnatura.tipocambio.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public final class PagosRateResolver {

    private static final int MAX_RETROCESO_DIAS = 14;

    private PagosRateResolver() {
    }

    public static BigDecimal resolverParaFecha(LocalDate fechaObjetivo, Map<LocalDate, BigDecimal> pagosPorFecha) {
        return BanxicoSeriesUtils.ultimoValorValido(
                fechaObjetivo, pagosPorFecha, MAX_RETROCESO_DIAS, "SF60653");
    }
}
