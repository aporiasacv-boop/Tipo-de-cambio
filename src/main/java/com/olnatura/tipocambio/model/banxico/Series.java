package com.olnatura.tipocambio.model.banxico;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Series {

    @JsonProperty("datos")
    private List<Dato> datos;
}
