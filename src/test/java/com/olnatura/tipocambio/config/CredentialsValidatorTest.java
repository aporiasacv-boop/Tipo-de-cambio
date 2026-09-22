package com.olnatura.tipocambio.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CredentialsValidatorTest {

    @Test
    void aceptaCredencialesCompletas() {
        CredentialsValidator validator = new CredentialsValidator(
                banxico("token-real"),
                dynamics("https://ejemplo.operations.dynamics.com", "tenant", "client", "secret"));

        assertDoesNotThrow(validator::validar);
    }

    @Test
    void rechazaPlaceholderDelEjemplo() {
        CredentialsValidator validator = new CredentialsValidator(
                banxico("PEGAR_TOKEN_BANXICO"),
                dynamics("https://ejemplo.operations.dynamics.com", "tenant", "client", "secret"));

        assertThrows(IllegalStateException.class, validator::validar);
    }

    private static BanxicoProperties banxico(String token) {
        BanxicoProperties properties = new BanxicoProperties();
        properties.setToken(token);
        return properties;
    }

    private static DynamicsProperties dynamics(
            String baseUrl, String tenantId, String clientId, String clientSecret) {
        DynamicsProperties properties = new DynamicsProperties();
        properties.setBaseUrl(baseUrl);
        properties.setTenantId(tenantId);
        properties.setClientId(clientId);
        properties.setClientSecret(clientSecret);
        return properties;
    }
}
