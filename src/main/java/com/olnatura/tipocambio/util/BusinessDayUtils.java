package com.olnatura.tipocambio.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class BusinessDayUtils {

    private BusinessDayUtils() {
    }

    public static boolean esFinDeSemana(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
    }

    public static LocalDate sumarDiasHabiles(LocalDate fecha, int dias) {
        if (dias < 0) {
            throw new IllegalArgumentException("dias debe ser >= 0");
        }
        LocalDate cursor = fecha;
        int restantes = dias;
        while (restantes > 0) {
            cursor = cursor.plusDays(1);
            if (!esFinDeSemana(cursor)) {
                restantes--;
            }
        }
        return cursor;
    }

    public static LocalDate restarDiasHabiles(LocalDate fecha, int dias) {
        if (dias < 0) {
            throw new IllegalArgumentException("dias debe ser >= 0");
        }
        LocalDate cursor = fecha;
        int restantes = dias;
        while (restantes > 0) {
            cursor = cursor.minusDays(1);
            if (!esFinDeSemana(cursor)) {
                restantes--;
            }
        }
        return cursor;
    }

    public static LocalDate ultimoViernes(LocalDate fecha) {
        LocalDate cursor = fecha;
        while (cursor.getDayOfWeek() != DayOfWeek.FRIDAY) {
            cursor = cursor.minusDays(1);
        }
        return cursor;
    }
}
