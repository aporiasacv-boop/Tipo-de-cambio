package com.olnatura.tipocambio.model.banxico;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BanxicoResponse {

    @JsonProperty("bmx")
    private Bmx bmx;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bmx {

        @JsonProperty("series")
        private java.util.List<Series> series;
    }
}
