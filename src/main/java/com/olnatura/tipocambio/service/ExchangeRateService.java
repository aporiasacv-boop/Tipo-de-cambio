package com.olnatura.tipocambio.service;

import com.olnatura.tipocambio.client.BanxicoClient;
import com.olnatura.tipocambio.client.ExchangeRatesClient;
import com.olnatura.tipocambio.model.ParDivisa;
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
    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");

    private final BanxicoClient banxicoClient;
    private final ExchangeRatesClient exchangeRatesClient;

    public void actualizarTipoCambio() {
        actualizarPar(ParDivisa.USD_MXN);
        actualizarPar(ParDivisa.EUR_MXN);
    }

    void actualizarPar(ParDivisa par) {
        LocalDate hoy = LocalDate.now(ZONA_MEXICO);
        LocalDate ventanaDesde = hoy.minusDays(DIAS_RETROCESO_OPERACION);
        Set<LocalDate> fechasExistentes = exchangeRatesClient.listarFechasDesde(par, ventanaDesde);

        LocalDate ultimoDynamics = fechasExistentes.stream().max(LocalDate::compareTo).orElse(null);
        log.info("Dynamics {} en ventana: {} fechas, ultimo: {}",
                par.etiqueta(), fechasExistentes.size(), ultimoDynamics);

        LocalDate banxicoDesde = ventanaDesde;
        LocalDate banxicoHasta = hoy.plusDays(MARGEN_CONSULTA_BANXICO_ADELANTO);

        Map<LocalDate, BigDecimal> tasasPorFecha = obtenerMapaBanxico(par, banxicoDesde, banxicoHasta);
        LocalDate ultimaFechaBanxico = ultimaFechaPublicada(tasasPorFecha);
        log.info("Banxico {}: {} fechas publicadas ({} a {}), ultima: {}",
                par.serieBanxico(), tasasPorFecha.size(), banxicoDesde, banxicoHasta, ultimaFechaBanxico);

        if (tasasPorFecha.isEmpty()) {
            throw new IllegalStateException("Banxico no devolvio datos para la serie " + par.serieBanxico());
        }

        LocalDate fechaFin = par.calcularFechaFin(hoy, ultimaFechaBanxico);
        LocalDate fechaDesde = ventanaDesde;
        List<LocalDate> fechasFaltantes = listarFechasFaltantesConDatoBanxico(
                fechasExistentes, tasasPorFecha, fechaDesde, fechaFin);

        log.info("{} hoy: {}, Banxico ultima: {}, rango: {} a {}, faltantes con dato API: {}",
                par.etiqueta(), hoy, ultimaFechaBanxico, fechaDesde, fechaFin, fechasFaltantes.size());

        if (fechasFaltantes.isEmpty()) {
            log.info("{} al dia hasta {}", par.etiqueta(), fechaFin);
            return;
        }

        int creados = 0;
        int errores = 0;

        for (LocalDate fecha : fechasFaltantes) {
            try {
                BigDecimal tasa = tasasPorFecha.get(fecha);
                if (tasa == null) {
                    log.debug("{} sin dato Banxico en {}, omitido", par.etiqueta(), fecha);
                    continue;
                }
                exchangeRatesClient.crearTipoCambio(par, tasa, fecha);
                log.info("Creado {}: {} ({})", fecha, tasa, par.serieBanxico());
                creados++;
            } catch (Exception e) {
                log.error("Error {} en {}: {}", par.etiqueta(), fecha, e.getMessage(), e);
                errores++;
            }
        }

        log.info("Resumen {}: {} creados, {} errores", par.etiqueta(), creados, errores);
        if (errores > 0) {
            throw new IllegalStateException(
                    par.etiqueta() + ": proceso terminado con " + errores + " error(es)");
        }
    }

    private Map<LocalDate, BigDecimal> obtenerMapaBanxico(ParDivisa par, LocalDate desde, LocalDate hasta) {
        if (ParDivisa.EUR_MXN.equals(par)) {
            return banxicoClient.obtenerMapaEuro(desde, hasta);
        }
        return banxicoClient.obtenerMapaPagos(desde, hasta);
    }

    static LocalDate ultimaFechaPublicada(Map<LocalDate, BigDecimal> pagosPorFecha) {
        return pagosPorFecha.keySet().stream()
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalStateException("Banxico sin fechas en la serie"));
    }

    static List<LocalDate> listarFechasFaltantesConDatoBanxico(
            Set<LocalDate> fechasExistentes,
            Map<LocalDate, BigDecimal> tasasPorFecha,
            LocalDate desde,
            LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            return List.of();
        }
        List<LocalDate> faltantes = new ArrayList<>();
        for (LocalDate fecha : tasasPorFecha.keySet()) {
            if (!fecha.isBefore(desde) && !fecha.isAfter(hasta) && !fechasExistentes.contains(fecha)) {
                faltantes.add(fecha);
            }
        }
        faltantes.sort(LocalDate::compareTo);
        return faltantes;
    }
}
