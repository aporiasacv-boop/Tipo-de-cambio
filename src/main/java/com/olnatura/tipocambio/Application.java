package com.olnatura.tipocambio;

import com.olnatura.tipocambio.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class Application {

    private final ExchangeRateService exchangeRateService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner runProcess() {
        return args -> {
            log.info("Inicio actualizacion USD/MXN SF60653");
            exchangeRateService.actualizarTipoCambio();
            log.info("Fin");
        };
    }
}
