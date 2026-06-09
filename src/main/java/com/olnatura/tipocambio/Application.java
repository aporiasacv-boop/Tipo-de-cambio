package com.olnatura.tipocambio;

import com.olnatura.tipocambio.service.ExchangeRateService;
import com.olnatura.tipocambio.util.ExitCodes;
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
        System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
    }

    @Bean
    CommandLineRunner runProcess() {
        return args -> {
            try {
                log.info("Inicio actualizacion USD/MXN (SF60653) y EUR/MXN (SF46410)");
                exchangeRateService.actualizarTipoCambio();
                log.info("CODIGO_SALIDA=0 - {}", ExitCodes.mensaje(ExitCodes.OK));
            } catch (Exception e) {
                int code = ExitCodes.fromThrowable(e);
                log.error("CODIGO_SALIDA={} - {}", code, ExitCodes.mensaje(code));
                log.error("Detalle: {}", e.getMessage());
                System.exit(code);
            }
        };
    }
}
