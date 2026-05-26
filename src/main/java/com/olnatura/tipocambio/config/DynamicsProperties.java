package com.olnatura.tipocambio.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "dynamics")
public class DynamicsProperties {

    private String baseUrl;
    private String tenantId;
    private String clientId;
    private String clientSecret;
    private String scope = "https://olnatura-produccion.operations.dynamics.com/.default";
}
