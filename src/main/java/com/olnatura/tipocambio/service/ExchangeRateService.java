package com.olnatura.tipocambio.service;

import com.olnatura.tipocambio.client.BanxicoClient;
import com.olnatura.tipocambio.client.ExchangeRatesClient;
import com.olnatura.tipocambio.util.PagosRateResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    static final int DIAS_RETROCESO_OPERACION = 14;
    private static final int MARGEN_CONSULTA_BANXICO_ADELANTO = 30;

    private static final String SERIE_PAGOS = BanxicoClient.SERIE_PAGOS;
    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");

    private final BanxicoClient banxicoClient;
    private final ExchangeRatesClient exchangeRatesClient;

    public void actualizarTipoCambio() {
        LocalDate hoy = LocalDate.now(ZONA_MEXICO);
        LocalDate ventanaDesde = hoy.minusDays(DIAS_RETROCESO_OPERACION);
        Set<LocalDate> fechasExistentes = exchangeRatesClient.listarFechasUsdMxnDesde(ventanaDesde);
        LocalDate ultimoDynamics = fechasExistentes.stream().max(LocalDate::compareTo).orElse(null);
        log.info("Dynamics USD/MXN en ventana: {} fechas, ultimo: {}", fechasExistentes.size(), ultimoDynamics);

        LocalDate banxicoDesde = ventanaDesde;
        LocalDate banxicoHasta = hoy.plusDays(MARGEN_CONSULTA_BANXICO_ADELANTO);

        Map<LocalDate, BigDecimal> pagosPorFecha =
                banxicoClient.obtenerMapaPagos(banxicoDesde, banxicoHasta);
        LocalDate ultimaFechaBanxico = ultimaFechaPublicada(pagosPorFecha);
        log.info("Banxico {}: {} fechas ({} a {}), ultima: {}",
                SERIE_PAGOS, pagosPorFecha.size(), banxicoDesde, banxicoHasta, ultimaFechaBanxico);

        if (pagosPorFecha.isEmpty()) {
            throw new IllegalStateException("Banxico no devolvio datos para la serie " + SERIE_PAGOS);
        }

        LocalDate fechaFin = ultimaFechaBanxico;
        LocalDate fechaDesde = calcularFechaDesde(hoy, fechasExistentes, fechaFin);
        List<LocalDate> fechasFaltantes = listarFechasFaltantes(fechasExistentes, fechaDesde, fechaFin);

        log.info("Hoy: {}, rango: {} a {}, faltantes: {}", hoy, fechaDesde, fechaFin, fechasFaltantes.size());

        if (fechasFaltantes.isEmpty()) {
            log.info("Dynamics al dia hasta {}", fechaFin);
            return;
        }

        int creados = 0;
        int errores = 0;

        for (LocalDate fecha : fechasFaltantes) {
            try {
                BigDecimal tasa = PagosRateResolver.resolverParaFecha(fecha, pagosPorFecha);
                exchangeRatesClient.crearTipoCambio(tasa, fecha);
                log.info("Creado {}: {} ({})", fecha, tasa, SERIE_PAGOS);
                creados++;
            } catch (Exception e) {
                log.error("Error en {}: {}", fecha, e.getMessage(), e);
                errores++;
            }
        }

        log.info("Resumen: {} creados, {} errores", creados, errores);
        if (errores > 0) {
            throw new IllegalStateException("Proceso terminado con " + errores + " error(es)");
        }
    }

    static LocalDate calcularFechaDesde(LocalDate hoy, Set<LocalDate> fechasExistentes, LocalDate fechaFinBanxico) {
        LocalDate limiteRetroceso = hoy.minusDays(DIAS_RETROCESO_OPERACION);
        if (fechasExistentes.isEmpty()) {
            return limiteRetroceso;
        }
        LocalDate ultimo = fechasExistentes.stream().max(LocalDate::compareTo).orElse(limiteRetroceso);
        LocalDate siguienteAlUltimo = ultimo.plusDays(1);
        if (siguienteAlUltimo.isBefore(limiteRetroceso)) {
            return limiteRetroceso;
        }
        if (siguienteAlUltimo.isAfter(fechaFinBanxico)) {
            return fechaFinBanxico;
        }
        return siguienteAlUltimo;
    }

    static LocalDate ultimaFechaPublicada(Map<LocalDate, BigDecimal> pagosPorFecha) {
        return pagosPorFecha.keySet().stream()
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalStateException("Banxico sin fechas en la serie"));
    }

    static List<LocalDate> listarFechasFaltantes(Set<LocalDate> fechasExistentes, LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            return List.of();
        }
        List<LocalDate> faltantes = new ArrayList<>();
        for (LocalDate cursor = desde; !cursor.isAfter(hasta); cursor = cursor.plusDays(1)) {
            if (!fechasExistentes.contains(cursor)) {
                faltantes.add(cursor);
            }
        }
        return faltantes;
    }
}
