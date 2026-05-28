package com.olnatura.tipocambio.service;

import com.olnatura.tipocambio.client.BanxicoClient;
import com.olnatura.tipocambio.client.ExchangeRatesClient;
import com.olnatura.tipocambio.model.dynamics.ExchangeRateRecord;
import com.olnatura.tipocambio.util.BusinessDayUtils;
import com.olnatura.tipocambio.util.DateUtils;
import com.olnatura.tipocambio.util.FixRateResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private static final String SERIE_FIX = "SF43718";
    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");
    private static final int DIAS_ADELANTO_HABILES = 2;
    private static final int DIAS_RETROCESO_BANXICO = 60;
    private static final int DIAS_INICIO_SIN_REGISTROS = 30;

    private final BanxicoClient banxicoClient;
    private final ExchangeRatesClient exchangeRatesClient;

    public void actualizarTipoCambio() {
        LocalDate hoy = LocalDate.now(ZONA_MEXICO);
        LocalDate fechaFin = BusinessDayUtils.sumarDiasHabiles(hoy, DIAS_ADELANTO_HABILES);

        List<ExchangeRateRecord> registros = exchangeRatesClient.listarTiposCambioUsdMxn();
        Set<LocalDate> fechasExistentes = extraerFechas(registros);
        LocalDate fechaInicio = calcularFechaInicio(fechasExistentes, hoy);

        log.info("Hoy (Mexico): {}, rango a cubrir: {} a {}", hoy, fechaInicio, fechaFin);

        if (fechaInicio.isAfter(fechaFin)) {
            log.info("Dynamics ya cubre hasta {} (objetivo {}). Sin fechas nuevas.",
                    fechasExistentes.stream().max(LocalDate::compareTo).orElse(null), fechaFin);
            return;
        }

        LocalDate banxicoDesde = fechaInicio.minusDays(DIAS_RETROCESO_BANXICO);
        LocalDate banxicoHasta = hoy;
        Map<LocalDate, BigDecimal> fixPorFecha = banxicoClient.obtenerMapaFix(banxicoDesde, banxicoHasta);
        log.info("FIX Banxico ({}): {} fechas con valor numerico", SERIE_FIX, fixPorFecha.size());

        int creados = 0;
        int omitidos = 0;
        int errores = 0;

        for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaFin); fecha = fecha.plusDays(1)) {
            if (fechasExistentes.contains(fecha)) {
                log.info("Omitido {}: ya existe en Dynamics", fecha);
                omitidos++;
                continue;
            }
            try {
                BigDecimal tasa = FixRateResolver.resolverParaFecha(fecha, fixPorFecha);
                exchangeRatesClient.crearTipoCambio(tasa, fecha);
                log.info("Creado {}: tasa {} (StartDate {})", fecha, tasa, DateUtils.toDynamicsStartDate(fecha));
                creados++;
            } catch (Exception e) {
                log.error("Error en fecha {}: {}", fecha, e.getMessage(), e);
                errores++;
            }
        }

        log.info("Resumen: {} creados, {} omitidos, {} errores", creados, omitidos, errores);
        if (errores > 0) {
            throw new IllegalStateException("Proceso terminado con " + errores + " error(es)");
        }
    }

    private LocalDate calcularFechaInicio(Set<LocalDate> fechasExistentes, LocalDate hoy) {
        Optional<LocalDate> ultima = fechasExistentes.stream().max(LocalDate::compareTo);
        if (ultima.isPresent()) {
            return ultima.get().plusDays(1);
        }
        return hoy.minusDays(DIAS_INICIO_SIN_REGISTROS);
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
