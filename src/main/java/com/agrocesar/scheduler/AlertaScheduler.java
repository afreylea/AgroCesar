package com.agrocesar.scheduler;

import com.agrocesar.model.Alerta;
import com.agrocesar.model.Usuario;
import com.agrocesar.service.AlertaService;
import com.agrocesar.service.SmsService;
import com.agrocesar.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.AlternativeJdkIdGenerator;

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

    private final AlertaService alertaService;
    private final SmsService smsService;
    private final UsuarioService usuarioService;

    public AlertaScheduler(AlertaService alertaService,SmsService smsService, UsuarioService usuarioService){
        this.alertaService = alertaService;
        this.smsService = smsService;
        this.usuarioService = usuarioService;
    }

    
    

}
