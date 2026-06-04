package com.olnatura.tipocambio.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CredentialsValidator {

    private final BanxicoProperties banxicoProperties;
    private final DynamicsProperties dynamicsProperties;

    public CredentialsValidator(BanxicoProperties banxicoProperties, DynamicsProperties dynamicsProperties) {
        this.banxicoProperties = banxicoProperties;
        this.dynamicsProperties = dynamicsProperties;
    }

    @PostConstruct
    void validar() {
        validarCampo("BANXICO_TOKEN", banxicoProperties.getToken());
        validarCampo("DYNAMICS_BASE_URL", dynamicsProperties.getBaseUrl());
        validarCampo("DYNAMICS_TENANT_ID", dynamicsProperties.getTenantId());
        validarCampo("DYNAMICS_CLIENT_ID", dynamicsProperties.getClientId());
        validarCampo("DYNAMICS_CLIENT_SECRET", dynamicsProperties.getClientSecret());
    }

    private static void validarCampo(String nombre, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Falta variable de entorno: " + nombre);
        }
        if (valor.startsWith("${")) {
            throw new IllegalStateException(
                    "Credencial sin valor: " + nombre + ". Revisa application.yml o variables de entorno.");
        }
    }
}
