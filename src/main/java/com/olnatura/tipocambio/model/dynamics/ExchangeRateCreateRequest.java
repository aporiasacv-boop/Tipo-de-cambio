package com.olnatura.tipocambio.model.dynamics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Cuerpo de POST a ExchangeRates. Sin metadatos OData (@odata.etag).
 */
@Data
public class ExchangeRateCreateRequest {

    @JsonProperty("RateTypeName")
    private String rateTypeName;

    @JsonProperty("FromCurrency")
    private String fromCurrency;

    @JsonProperty("ToCurrency")
    private String toCurrency;

    @JsonProperty("StartDate")
    private String startDate;

    @JsonProperty("Rate")
    private Object rate;

    @JsonProperty("ConversionFactor")
    private String conversionFactor;
}
