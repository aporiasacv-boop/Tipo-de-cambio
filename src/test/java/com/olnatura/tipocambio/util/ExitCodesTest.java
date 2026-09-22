package com.olnatura.tipocambio.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExitCodesTest {

    @Test
    void clasificaCredenciales() {
        int code = ExitCodes.fromThrowable(
                new IllegalStateException("Credencial sin valor: BANXICO_TOKEN"));
        assertEquals(ExitCodes.CREDENCIALES, code);
    }

    @Test
    void clasificaBanxico() {
        int code = ExitCodes.fromThrowable(
                new IllegalStateException("Banxico no devolvio datos para la serie SF46410"));
        assertEquals(ExitCodes.BANXICO, code);
    }

    @Test
    void clasificaDynamics() {
        int code = ExitCodes.fromThrowable(
                new IllegalStateException("Error OData Dynamics 401"));
        assertEquals(ExitCodes.DYNAMICS, code);
    }

    @Test
    void clasificaProcesoParcial() {
        int code = ExitCodes.fromThrowable(
                new IllegalStateException("EUR/MXN: proceso terminado con 2 error(es)"));
        assertEquals(ExitCodes.PROCESO_PARCIAL, code);
    }

    @Test
    void clasificaFaltaCredencial() {
        int code = ExitCodes.fromThrowable(
                new IllegalStateException("Falta credencial: BANXICO_TOKEN"));
        assertEquals(ExitCodes.CREDENCIALES, code);
    }

    @Test
    void mensajeBanxicoMencionaAmbasSeries() {
        String mensaje = ExitCodes.mensaje(ExitCodes.BANXICO);
        assertTrue(mensaje.contains("SF60653"));
        assertTrue(mensaje.contains("SF46410"));
    }
}
