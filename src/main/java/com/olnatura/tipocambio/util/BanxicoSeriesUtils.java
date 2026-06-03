package com.olnatura.tipocambio.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public final class BanxicoSeriesUtils {

    private BanxicoSeriesUtils() {
    }

    public static BigDecimal ultimoValorValido(
            LocalDate fechaObjetivo,
            Map<LocalDate, BigDecimal> valoresPorFecha,
            int maxRetrocesoDias,
            String nombreSerie) {
        LocalDate cursor = fechaObjetivo;
        LocalDate limite = fechaObjetivo.minusDays(maxRetrocesoDias);
        while (!cursor.isBefore(limite)) {
            BigDecimal tasa = valoresPorFecha.get(cursor);
            if (tasa != null) {
                return tasa;
            }
            cursor = cursor.minusDays(1);
        }
        throw new IllegalStateException("Sin valor " + nombreSerie + " en " + maxRetrocesoDias
                + " dias desde " + fechaObjetivo);
    }
}
