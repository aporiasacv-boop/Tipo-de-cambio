package com.olnatura.tipocambio.service;

import com.olnatura.tipocambio.client.BanxicoClient;
import com.olnatura.tipocambio.client.ExchangeRatesClient;
import com.olnatura.tipocambio.model.dynamics.ExchangeRateRecord;
import com.olnatura.tipocambio.util.DateUtils;
import com.olnatura.tipocambio.util.PagosRateResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private static final String SERIE_PAGOS = BanxicoClient.SERIE_PAGOS;
    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");

    /** Primera fecha del historial USD/MXN en Dynamics (carga inicial). */
    static final LocalDate FECHA_INICIO_HISTORIAL = LocalDate.of(2020, 2, 29);

    /** Margen al consultar Banxico hacia el futuro para detectar la ultima fecha publicada en SF60653. */
    private static final int MARGEN_CONSULTA_BANXICO_ADELANTO = 30;
    private static final int DIAS_RETROCESO_BANXICO = 60;

    private final BanxicoClient banxicoClient;
    private final ExchangeRatesClient exchangeRatesClient;

    public void actualizarTipoCambio() {
        LocalDate hoy = LocalDate.now(ZONA_MEXICO);
        List<ExchangeRateRecord> registros = exchangeRatesClient.listarTiposCambioUsdMxn();
        Set<LocalDate> fechasExistentes = extraerFechas(registros);
        boolean cargaHistorialCompleta = fechasExistentes.isEmpty();

        LocalDate banxicoHasta = hoy.plusDays(MARGEN_CONSULTA_BANXICO_ADELANTO);
        LocalDate banxicoDesde = cargaHistorialCompleta
                ? FECHA_INICIO_HISTORIAL
                : fechasExistentes.stream()
                        .min(LocalDate::compareTo)
                        .orElse(FECHA_INICIO_HISTORIAL)
                        .minusDays(DIAS_RETROCESO_BANXICO);
        if (banxicoDesde.isBefore(FECHA_INICIO_HISTORIAL)) {
            banxicoDesde = FECHA_INICIO_HISTORIAL;
        }

        Map<LocalDate, BigDecimal> pagosPorFecha =
                banxicoClient.obtenerMapaPagos(banxicoDesde, banxicoHasta);
        LocalDate ultimaFechaBanxico = ultimaFechaPublicada(pagosPorFecha);
        log.info("Para pagos Banxico ({}): {} fechas con valor numerico (consulta {} a {}), ultima publicada: {}",
                SERIE_PAGOS, pagosPorFecha.size(), banxicoDesde, banxicoHasta, ultimaFechaBanxico);

        if (pagosPorFecha.isEmpty()) {
            throw new IllegalStateException("Banxico no devolvio datos para la serie " + SERIE_PAGOS);
        }

        LocalDate fechaFin = ultimaFechaBanxico;
        LocalDate fechaDesde = calcularFechaDesde(fechasExistentes, fechaFin);
        List<LocalDate> fechasFaltantes = listarFechasFaltantes(fechasExistentes, fechaDesde, fechaFin);

        log.info("Hoy (Mexico): {}, rango a cubrir: {} a {} (hasta ultima {}), fechas faltantes: {}",
                hoy, fechaDesde, fechaFin, SERIE_PAGOS, fechasFaltantes.size());

        if (fechasFaltantes.isEmpty()) {
            log.info("Dynamics ya cubre el rango {} a {}. Sin fechas nuevas.", fechaDesde, fechaFin);
            return;
        }

        int creados = 0;
        int errores = 0;

        for (LocalDate fecha : fechasFaltantes) {
            try {
                BigDecimal tasa = PagosRateResolver.resolverParaFecha(fecha, pagosPorFecha);
                exchangeRatesClient.crearTipoCambio(tasa, fecha);
                log.info("Creado {}: tasa {} (pagos {}) (StartDate {})",
                        fecha, tasa, SERIE_PAGOS, DateUtils.toDynamicsStartDate(fecha));
                creados++;
            } catch (Exception e) {
                log.error("Error en fecha {}: {}", fecha, e.getMessage(), e);
                errores++;
            }
        }

        log.info("Resumen: {} creados, {} errores", creados, errores);
        if (errores > 0) {
            throw new IllegalStateException("Proceso terminado con " + errores + " error(es)");
        }
    }

    /**
     * Si solo faltan dias al final, empieza en ultimo+1; si hay huecos en medio, revisa desde el minimo.
     */
    /**
     * Carga inicial: desde {@link #FECHA_INICIO_HISTORIAL}.
     * Operacion diaria: desde ultimo+1 o desde el minimo si hay huecos.
     */
    private LocalDate calcularFechaDesde(Set<LocalDate> fechasExistentes, LocalDate fechaFinBanxico) {
        if (fechasExistentes.isEmpty()) {
            return FECHA_INICIO_HISTORIAL;
        }
        LocalDate ultimo = fechasExistentes.stream().max(LocalDate::compareTo).orElse(FECHA_INICIO_HISTORIAL);
        LocalDate siguienteAlUltimo = ultimo.plusDays(1);
        if (!siguienteAlUltimo.isAfter(fechaFinBanxico)) {
            return siguienteAlUltimo;
        }
        return fechasExistentes.stream().min(LocalDate::compareTo).orElse(FECHA_INICIO_HISTORIAL);
    }

    static LocalDate ultimaFechaPublicada(Map<LocalDate, BigDecimal> pagosPorFecha) {
        return pagosPorFecha.keySet().stream()
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalStateException("Banxico no devolvio fechas en la serie"));
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

    private Set<LocalDate> extraerFechas(List<ExchangeRateRecord> registros) {
        Set<LocalDate> fechas = new HashSet<>();
        for (ExchangeRateRecord registro : registros) {
            try {
                fechas.add(DateUtils.parseDynamicsStartDate(registro.getStartDate()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return fechas;
    }
}
