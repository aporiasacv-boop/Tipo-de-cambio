package com.olnatura.tipocambio.model.dynamics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateRecord {

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
