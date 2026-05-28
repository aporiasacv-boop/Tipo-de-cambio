package com.olnatura.tipocambio.client;

import com.olnatura.tipocambio.config.DynamicsProperties;
import com.olnatura.tipocambio.model.dynamics.ExchangeRateRecord;
import com.olnatura.tipocambio.model.dynamics.ExchangeRatesODataResponse;
import com.olnatura.tipocambio.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExchangeRatesClient {

    private static final String FILTER =
            "/data/ExchangeRates?$filter=FromCurrency eq 'USD' and ToCurrency eq 'MXN'";

    private final RestClient restClient;
    private final DynamicsProperties dynamicsProperties;
    private final DynamicsAuthClient dynamicsAuthClient;

    public List<ExchangeRateRecord> listarTiposCambioUsdMxn() {
        String accessToken = dynamicsAuthClient.obtenerAccessToken();
        String url = normalizarBaseUrl() + FILTER;

        ExchangeRatesODataResponse response = restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ExchangeRatesODataResponse.class);

        if (response == null || response.getValue() == null) {
            return Collections.emptyList();
        }
        return response.getValue();
    }

    public void crearTipoCambio(BigDecimal rate, LocalDate fecha) {
        String accessToken = dynamicsAuthClient.obtenerAccessToken();
        String url = normalizarBaseUrl() + "/data/ExchangeRates";

        ExchangeRateRecord payload = buildExchangeRatePayload(rate, fecha);

        restClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    static ExchangeRateRecord buildExchangeRatePayload(BigDecimal rate, LocalDate fecha) {
        ExchangeRateRecord record = new ExchangeRateRecord();
        record.setRateTypeName("Predeterminado");
        record.setFromCurrency("USD");
        record.setToCurrency("MXN");
        record.setStartDate(DateUtils.toDynamicsStartDate(fecha));
        record.setRate(rate);
        record.setConversionFactor("One");
        return record;
    }

    private String normalizarBaseUrl() {
        String baseUrl = dynamicsProperties.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("dynamics.base-url no configurado");
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
