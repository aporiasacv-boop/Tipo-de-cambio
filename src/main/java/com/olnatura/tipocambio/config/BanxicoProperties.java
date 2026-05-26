package com.olnatura.tipocambio.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "banxico")
public class BanxicoProperties {

    private String token;
}
