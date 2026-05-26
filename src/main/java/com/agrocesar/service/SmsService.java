package com.agrocesar.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

/**
 * Servicio de envio de mensajes SMS mediante la API de Twilio.
 *
 * Encapsula la integracion con Twilio SDK para notificar al agricultor
 * cuando el motor de alertas genera una alerta climatica. Un fallo en el
 * envio es capturado y logueado sin interrumpir el flujo principal (RNF05).
 *
 * Patron aplicado: Service Layer. El AlertaScheduler invoca enviarSms()
 * sin conocer los detalles internos de la API de Twilio.
 */
@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    /**
     * SID de la cuenta Twilio. Se inyecta desde application-local.properties.
     * Propiedad: twilio.account-sid
     */
    @Value("${twilio.account-sid}")
    private String accountSid;

    /**
     * Token de autenticacion de la cuenta Twilio. Se inyecta desde
     * application-local.properties.
     * Propiedad: twilio.auth-token
     */
    @Value("${twilio.auth-token}")
    private String authToken;

    /**
     * Numero de telefono remitente registrado en Twilio.
     * Propiedad: twilio.from-number
     */
    @Value("${twilio.from-number}")
    private String fromNumber;

    /**
     * Inicializa el cliente de Twilio con las credenciales inyectadas.
     * Se ejecuta automaticamente al levantar el contexto de Spring.
     * Si las credenciales son invalidas, el error se propaga al arranque.
     */
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        log.info("SmsService inicializado correctamente.");
    }

    /**
     * Envia un mensaje SMS al numero de telefono indicado.
     *
     * Si la llamada a la API de Twilio falla por cualquier razon, la excepcion
     * es capturada y logueada. El metodo no relanza la excepcion para cumplir
     * RNF05.
     *
     * @param telefono numero de telefono destino en formato E.164, por ejemplo
     *                 +573001234567
     * @param mensaje  texto del mensaje SMS a enviar
     */
    public void enviarSms(String telefono, String mensaje) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(telefono),
                    new PhoneNumber(fromNumber),
                    mensaje).create();
            log.info("SMS enviado a {} - SID: {}", telefono, message.getSid());
        } catch (Exception e) {
            log.error("Error al enviar SMS a {}: {}", telefono, e.getMessage());
        }
    }
}