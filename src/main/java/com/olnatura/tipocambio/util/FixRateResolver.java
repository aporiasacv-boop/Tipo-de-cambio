package com.olnatura.tipocambio.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public final class FixRateResolver {

    private static final int MAX_RETROCESO_DIAS = 90;

    private FixRateResolver() {
    }

    public static BigDecimal resolverParaFecha(LocalDate fechaObjetivo, Map<LocalDate, BigDecimal> fixPorFecha) {
        if (BusinessDayUtils.esFinDeSemana(fechaObjetivo)) {
            LocalDate viernes = BusinessDayUtils.ultimoViernes(fechaObjetivo);
            return resolverParaFecha(viernes, fixPorFecha);
        }
        LocalDate fechaFix = BusinessDayUtils.restarDiasHabiles(fechaObjetivo, 2);
        return ultimoFixValido(fechaFix, fixPorFecha);
    }

    static BigDecimal ultimoFixValido(LocalDate desde, Map<LocalDate, BigDecimal> fixPorFecha) {
        LocalDate cursor = desde;
        LocalDate limite = desde.minusDays(MAX_RETROCESO_DIAS);
        while (!cursor.isBefore(limite)) {
            BigDecimal tasa = fixPorFecha.get(cursor);
            if (tasa != null) {
                return tasa;
            }
            cursor = cursor.minusDays(1);
        }
        throw new IllegalStateException("No hay FIX valido en los ultimos " + MAX_RETROCESO_DIAS
                + " dias desde " + desde);
    }
}
