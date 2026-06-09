package com.olnatura.tipocambio.util;

public final class ExitCodes {

    public static final int OK = 0;
    public static final int CREDENCIALES = 10;
    public static final int BANXICO = 11;
    public static final int DYNAMICS = 12;
    public static final int PROCESO_PARCIAL = 13;
    public static final int INESPERADO = 99;

    private ExitCodes() {
    }

    public static int fromThrowable(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String msg = root.getMessage();
        if (msg == null) {
            msg = "";
        }
        String lower = msg.toLowerCase();

        if (lower.contains("credencial") || lower.contains("banxico_token")
                || lower.contains("dynamics_") || lower.contains("falta variable")) {
            return CREDENCIALES;
        }
        if (lower.contains("banxico")) {
            return BANXICO;
        }
        if (lower.contains("dynamics") || lower.contains("odata") || lower.contains("401")
                || lower.contains("403")) {
            return DYNAMICS;
        }
        if (lower.contains("proceso terminado con") || lower.contains("error(es)")) {
            return PROCESO_PARCIAL;
        }
        return INESPERADO;
    }

    public static String mensaje(int code) {
        return switch (code) {
            case OK -> "OK - ejecucion correcta";
            case CREDENCIALES -> "Credenciales faltantes o invalidas (revisa application-local.yml)";
            case BANXICO -> "Error al consultar Banxico (token, red o serie SF60653)";
            case DYNAMICS -> "Error al conectar o escribir en Dynamics";
            case PROCESO_PARCIAL -> "Algunas fechas fallaron al insertarse";
            case INESPERADO -> "Error inesperado - ver detalle arriba en el log";
            default -> "Codigo " + code + " - ver detalle en el log";
        };
    }
}
