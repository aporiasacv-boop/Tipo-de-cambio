package com.olnatura.tipocambio.client;

import com.olnatura.tipocambio.config.DynamicsProperties;
import com.olnatura.tipocambio.model.dynamics.ExchangeRateCreateRequest;
import com.olnatura.tipocambio.model.dynamics.ExchangeRateRecord;
import com.olnatura.tipocambio.model.dynamics.ExchangeRatesODataResponse;
import com.olnatura.tipocambio.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExchangeRatesClient {

    private final RestClient restClient;
    private final DynamicsProperties dynamicsProperties;
    private final DynamicsAuthClient dynamicsAuthClient;

    public Set<LocalDate> listarFechasUsdMxnDesde(LocalDate desde) {
        String accessToken = dynamicsAuthClient.obtenerAccessToken();
        Set<LocalDate> fechas = new HashSet<>();
        URI uri = uriListarDesde(desde);

        while (uri != null) {
            ExchangeRatesODataResponse response = restClient.get()
                    .uri(uri)
                    .header("Authorization", "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(ExchangeRatesODataResponse.class);

            if (response == null || response.getValue() == null) {
                break;
            }
            for (ExchangeRateRecord registro : response.getValue()) {
                try {
                    fechas.add(DateUtils.parseDynamicsStartDate(registro.getStartDate()));
                } catch (IllegalArgumentException ignored) {
                }
            }
            String nextLink = response.getNextLink();
            uri = nextLink == null || nextLink.isBlank() ? null : URI.create(nextLink);
        }
        return fechas;
    }

    static URI uriListarDesde(String baseUrl, LocalDate desde) {
        String filtro = String.format(
                "FromCurrency eq 'USD' and ToCurrency eq 'MXN' and StartDate ge %s",
                DateUtils.toDynamicsStartDate(desde));
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/data/ExchangeRates")
                .queryParam("$filter", filtro)
                .encode()
                .build()
                .toUri();
    }

    private URI uriListarDesde(LocalDate desde) {
        return uriListarDesde(normalizarBaseUrl(), desde);
    }

    public void crearTipoCambio(BigDecimal rate, LocalDate fecha) {
        String accessToken = dynamicsAuthClient.obtenerAccessToken();
        String url = normalizarBaseUrl() + "/data/ExchangeRates";

        ExchangeRateCreateRequest payload = buildExchangeRateCreateRequest(rate, fecha);

        restClient.post()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    static ExchangeRateCreateRequest buildExchangeRateCreateRequest(BigDecimal rate, LocalDate fecha) {
        ExchangeRateCreateRequest request = new ExchangeRateCreateRequest();
        request.setRateTypeName("Predeterminado");
        request.setFromCurrency("USD");
        request.setToCurrency("MXN");
        request.setStartDate(DateUtils.toDynamicsStartDate(fecha));
        request.setRate(rate);
        request.setConversionFactor("One");
        return request;
    }

    private String normalizarBaseUrl() {
        String baseUrl = dynamicsProperties.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("dynamics.base-url no configurado");
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
