package com.agrocesar.service;

import com.agrocesar.dto.CultivoConUmbralesDTO;
import com.agrocesar.dto.DailyForecast;
import com.agrocesar.model.Alerta;
import com.agrocesar.repository.AlertaRepository;
import com.agrocesar.repository.CultivoConUmbralesRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class MotorAlertaService {
    private final AlertaRepository alertaRepository;
    private final CultivoConUmbralesRepository cultivoRepo;
    private final WeatherService weatherService;
    private final Map<String, SeveridadStrategy> estrategias;  // inyectado por Spring
    private final RecomendacionService recomendacionService;
    private final SmsService smsService;

    public MotorAlertaService(AlertaRepository alertaRepository,
                        CultivoConUmbralesRepository cultivoRepo,
                        WeatherService weatherService,
                        Map<String, SeveridadStrategy> estrategias,
                        RecomendacionService recomendacionService,
                        SmsService smsService) {
        
        this.alertaRepository = alertaRepository;
        this.cultivoRepo = cultivoRepo;
        this.estrategias = estrategias;
        this.recomendacionService = recomendacionService;
        this.smsService = smsService;
        this.weatherService = weatherService;
    }

    private static final Logger log = LoggerFactory.getLogger(MotorAlertaService.class);

    /**
     * Punto de entrada del motor de alertas.
     * Invocado por AlertaScheduler a las 6AM y 6PM.
     * Para cada cultivo activo consulta el pronóstico de 7 días y
     * genera alertas si algún valor supera los umbrales efectivos.
     */
    public void ejecutarMotor() {
        log.info("=== Motor de alertas iniciado ===");
        List<CultivoConUmbralesDTO> cultivos = cultivoRepo.findAll();
        log.info("Cultivos activos a evaluar: {}", cultivos.size());

        for (CultivoConUmbralesDTO cultivo : cultivos) {
            procesarCultivo(cultivo);
        }
        log.info("=== Motor de alertas finalizado ===");
    }

    private void procesarCultivo(CultivoConUmbralesDTO cultivo) {
        List<DailyForecast> pronostico = weatherService.obtenerPronostico7Dias(cultivo.getLatitud(), cultivo.getLongitud());

        if (pronostico.isEmpty()) {
            log.warn("Sin pronóstico para cultivo id={} ({})", cultivo.getId(), cultivo.getCultivo());
            return;
        }

        for (DailyForecast dia : pronostico) {
            evaluarDia(cultivo, dia);
        }
    }

    private void evaluarDia(CultivoConUmbralesDTO cultivo, DailyForecast dia) {
        LocalDate fechaDia = LocalDate.parse(dia.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE);
        
        // Los 6 tipos de alerta posibles
        if (cultivo.getTempMaxEfectiva() != null && dia.getTempMax() != null) 
            verificarUmbral(cultivo, dia, fechaDia, "TEMPERATURA_ALTA",    
                            dia.getTempMax(),   cultivo.getTempMaxEfectiva(), true);

        if (cultivo.getTempMinEfectiva() != null && dia.getTempMin() != null)
            verificarUmbral(cultivo, dia, fechaDia, "TEMPERATURA_BAJA",    
                            dia.getTempMin(),   cultivo.getTempMinEfectiva(), false);

        if (cultivo.getLluviaMaxEfectiva() != null && dia.getLluviaMm() != null)
            verificarUmbral(cultivo, dia, fechaDia, "LLUVIA_EXCESIVA",     
                            dia.getLluviaMm(),  cultivo.getLluviaMaxEfectiva(), true);

        if (cultivo.getLluviaMinEfectiva() != null && dia.getLluviaMm() != null)
            verificarUmbral(cultivo, dia, fechaDia, "LLUVIA_INSUFICIENTE", 
                            dia.getLluviaMm(),  cultivo.getLluviaMinEfectiva(), false);

        if (cultivo.getHumedadMaxEfectiva() != null && dia.getHumedadMax() != null)
            verificarUmbral(cultivo, dia, fechaDia, "HUMEDAD_EXCESIVA",    
                            dia.getHumedadMax(), cultivo.getHumedadMaxEfectiva(), true);

        if (cultivo.getHumedadMinEfectiva() != null && dia.getHumedadMin() != null)
            verificarUmbral(cultivo, dia, fechaDia, "HUMEDAD_INSUFICIENTE",
                            dia.getHumedadMin(), cultivo.getHumedadMinEfectiva(), false);
    }

    /**
     * @param superaUmbral true = alerta cuando valorDetectado > umbral (excesivo)
     *                     false = alerta cuando valorDetectado < umbral (insuficiente)
     */
    private void verificarUmbral(CultivoConUmbralesDTO cultivo, DailyForecast dia,
                                LocalDate fechaDia, String tipoAlerta,
                                double valorDetectado, double valorUmbral,
                                boolean superaUmbral) {

        boolean disparar = superaUmbral
                ? valorDetectado > valorUmbral
                : valorDetectado < valorUmbral;

        if (!disparar) return;

        SeveridadStrategy estrategia = estrategias.get(cultivo.getCategoria());
        String severidad = estrategia.calcular(
                cultivo.getDiasRestantes(), cultivo.getDiasCosechaProm());

        String descripcion = String.format(
            "Alerta %s en cultivo de %s (%s). Valor: %.2f — Umbral: %.2f",
            tipoAlerta, cultivo.getCultivo(), cultivo.getMunicipio(),
            valorDetectado, valorUmbral
        );

        Alerta alerta = Alerta.builder()
                .cultivoAgricultorId(cultivo.getId())
                .tipoAlerta(tipoAlerta)
                .severidad(severidad)
                .descripcion(descripcion)
                .fechaDiaPronostico(fechaDia)
                .valorDetectado(valorDetectado)
                .valorUmbral(valorUmbral)
                .recomendacion(null)
                .leida(0)
                .build();

        persistirAlerta(alerta, cultivo);
    }

    private void persistirAlerta(Alerta alerta, CultivoConUmbralesDTO cultivo) {
        try {
            // 1. Genera recomendación (fallo no interrumpe)
            try {
                String recomendacion = recomendacionService.generar(
                    alerta.getTipoAlerta(), cultivo.getCultivo(), alerta.getValorDetectado(),
                    alerta.getSeveridad(), cultivo.getDiasRestantes(), cultivo.getDiasCosechaProm());

                alerta.setRecomendacion(recomendacion);
            } catch (Exception e) {
                log.warn("RecomendacionService falló, alerta se persiste sin recomendación: {}", e.getMessage());
            }

            // 2. Persiste la alerta (UQ_ALERTAS_NODUP evita duplicados)
            alertaRepository.insert(alerta);
            log.info("Alerta generada: {} - {} - {}", alerta.getTipoAlerta(),
                    alerta.getSeveridad(), alerta.getFechaDiaPronostico());

            // 3. Envía SMS (fallo no interrumpe)
            if (cultivo.getTelefono() != null && !cultivo.getTelefono().isBlank()) {
                try {
                    String sms = String.format("[AgroCesar] Apreciado %s, se ha detectado %s para su cultivo de %s. Severidad: %s",
                            cultivo.getAgricultor(), alerta.getTipoAlerta(), cultivo.getCultivo(), alerta.getSeveridad());
                    smsService.enviarSms(cultivo.getTelefono(), sms);
                } catch (Exception e) {
                    log.warn("SmsService falló: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            // UQ_ALERTAS_NODUP lanza excepción si ya existe la alerta para ese día — ignorar
            if (e.getMessage() != null && e.getMessage().contains("UQ_ALERTAS_NODUP")) {
                log.debug("Alerta duplicada ignorada: {} - {} - {}",
                    alerta.getTipoAlerta(), alerta.getCultivoAgricultorId(),
                    alerta.getFechaDiaPronostico());
            } else {
                log.error("Error al persistir alerta: {}", e.getMessage());
            }
        }
    }
}
