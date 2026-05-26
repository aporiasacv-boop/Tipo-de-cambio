package com.olnatura.tipocambio.service;

import com.olnatura.tipocambio.client.BanxicoClient;
import com.olnatura.tipocambio.client.ExchangeRatesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final BanxicoClient banxicoClient;
    private final ExchangeRatesClient exchangeRatesClient;

    public void actualizarTipoCambio() {
        BigDecimal tipoCambio = banxicoClient.obtenerTipoCambioParaPagos();
        log.info("Tipo de cambio obtenido desde Banxico: {}", tipoCambio);

        LocalDate hoy = LocalDate.now();
        boolean existe = exchangeRatesClient.existeParaFecha(hoy);
        log.info("Existencia previa para la fecha {}: {}", hoy, existe);

        if (existe) {
            log.info("Tipo de cambio ya existe para hoy");
            return;
        }

        exchangeRatesClient.crearTipoCambio(tipoCambio, hoy);
        log.info("Registro creado en ExchangeRates para USD -> MXN con tasa {} y fecha {}", tipoCambio, hoy);
    }
}
