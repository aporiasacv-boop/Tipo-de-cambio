package com.olnatura.tipocambio.config;

import com.olnatura.tipocambio.util.ExitCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class StartupFailureHandler implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        int code = ExitCodes.fromThrowable(event.getException());
        log.error("CODIGO_SALIDA={} - {}", code, ExitCodes.mensaje(code));
        System.exit(code);
    }
}
