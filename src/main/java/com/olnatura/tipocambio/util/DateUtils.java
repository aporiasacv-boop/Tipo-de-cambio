package com.olnatura.tipocambio.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtils {

    private static final DateTimeFormatter BANXICO_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DYNAMICS_START_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private DateUtils() {
    }

    public static LocalDate parseBanxicoDate(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException("Fecha Banxico vacía");
        }
        try {
            return LocalDate.parse(fecha.trim(), BANXICO_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha Banxico inválida: " + fecha, e);
        }
    }

    public static LocalDate parseDynamicsStartDate(String startDate) {
        if (startDate == null || startDate.isBlank()) {
            throw new IllegalArgumentException("StartDate Dynamics vacío");
        }
        String value = startDate.trim();
        try {
            if (value.contains("T")) {
                if (value.endsWith("Z")) {
                    return Instant.parse(value).atZone(ZoneOffset.UTC).toLocalDate();
                }
                return OffsetDateTime.parse(value).toLocalDate();
            }
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(value).toLocalDate();
            } catch (DateTimeParseException ignored) {
                throw new IllegalArgumentException("StartDate Dynamics inválido: " + startDate, e);
            }
        }
    }

    public static String toDynamicsStartDate(LocalDate date) {
        return date.atTime(12, 0).atOffset(ZoneOffset.UTC).format(DYNAMICS_START_DATE);
    }

    public static boolean isSameDate(LocalDate a, LocalDate b) {
        return a != null && a.equals(b);
    }
}
