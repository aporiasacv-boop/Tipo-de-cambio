package com.olnatura.tipocambio.model.dynamics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRatesODataResponse {

    @JsonProperty("value")
    private List<ExchangeRateRecord> value;
}
