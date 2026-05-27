package com.agrocesar.scheduler;

import com.agrocesar.service.MotorAlertaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @AlertaScheduler es un componente Spring que invoca automaticamente
 *                  el motor de alertas de Dev 1 dos veces al dia: a las 6AM y a
 *                  las 6PM.
 * @Logger componente esencial para registrar logs sobre el comportamiento y la
 *         ejecucion
 *         de la aplicacion en tiempo real.
 * @LoggerFactory crea o recuper instancias de los loggers, para rastrear la
 *                ejecucion de la app.
 */
@Component
public class AlertaScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertaScheduler.class);

    private final MotorAlertaService motorAlertaService;

    public AlertaScheduler(MotorAlertaService motorAlertaService) {
        this.motorAlertaService = motorAlertaService;

    }

    /**
     * @Scheduled Indica cuantas veces se ejecturara dicho metodo
     * @cron
     *       0 = El segundo en el que inicia
     *       0 = El minuto de la ejecucion
     *       11 = La hora en formato 24 horas
     *       * = Cualquier dia del mes
     *       * = Cualquier mes
     *       * = Cualquier dia de la semana
     */
    @Scheduled(cron = " 0 0 11 * * *", zone = "America/Bogota")
    public void ejecutarManiana() {
        log.info("[Scheduler] Ejecucion matutina 6AM");
        ejecutarCiclo();
    }

    @Scheduled(cron = " 0 0 23 * * *", zone = "America/Bogota")
    public void ejecutarTarde() {
        log.info("[Scheduler] Ejecucion vespertina 6PM");
        ejecutarCiclo();

    }

    private void ejecutarCiclo() {
        try {
            motorAlertaService.ejecutarMotor();
            log.info("[Scheduler] Ciclo de alertas ejecutado correctamente");
        } catch (Exception e) {
            log.error("[Scheduler] Error en el ciclo de alertas: {} ", e.getMessage());
        }
    }

}
