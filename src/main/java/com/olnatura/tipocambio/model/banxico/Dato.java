package com.olnatura.tipocambio.model.banxico;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dato {

    @JsonProperty("fecha")
    private String fecha;

    @JsonProperty("dato")
    private String dato;
}
