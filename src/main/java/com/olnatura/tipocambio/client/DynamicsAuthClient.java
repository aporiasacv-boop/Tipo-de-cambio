package com.olnatura.tipocambio.client;

import com.olnatura.tipocambio.config.DynamicsProperties;
import com.olnatura.tipocambio.model.dynamics.DynamicsTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class DynamicsAuthClient {

    private final RestClient restClient;
    private final DynamicsProperties dynamicsProperties;

    public String obtenerAccessToken() {
        String tokenUrl = String.format(
                "https://login.microsoftonline.com/%s/oauth2/v2.0/token",
                dynamicsProperties.getTenantId());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", dynamicsProperties.getClientId());
        body.add("client_secret", dynamicsProperties.getClientSecret());
        body.add("scope", dynamicsProperties.getScope());
        body.add("grant_type", "client_credentials");

        DynamicsTokenResponse response = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(DynamicsTokenResponse.class);

        if (response == null || response.getAccessToken() == null || response.getAccessToken().isBlank()) {
            throw new IllegalStateException("No se obtuvo access_token de Dynamics");
        }
        return response.getAccessToken();
    }
}
