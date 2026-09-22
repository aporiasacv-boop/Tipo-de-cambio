package com.olnatura.tipocambio.model;

import com.olnatura.tipocambio.client.BanxicoClient;

public record ParDivisa(
        String fromCurrency,
        String toCurrency,
        String serieBanxico) {

    public static final ParDivisa USD_MXN = new ParDivisa(
            "USD", "MXN", BanxicoClient.SERIE_PAGOS);

    public static final ParDivisa EUR_MXN = new ParDivisa(
            "EUR", "MXN", BanxicoClient.SERIE_EURO);

    public String etiqueta() {
        return fromCurrency + "/" + toCurrency;
    }
}
