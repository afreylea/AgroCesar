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

    private static final Logger log = LoggerFactory.getLogger(MotorAlertaService.class);

    /** Porcentaje del umbral quincenal usado como tolerancia para lluvia insuficiente. */
    private static final double LLUVIA_TOLERANCIA_PCT = 0.10;

    /** Tolerancia mínima absoluta en mm, aplicada cuando el 10% del umbral es menor a este valor.
     *  Evita que cultivos con requerimientos hídricos bajos sean demasiado permisivos. */
    private static final double LLUVIA_TOLERANCIA_MIN = 5.0;

    /**
     * Tolerancia fija para temperatura (°C).
     * Reduce falsas alarmas por variaciones naturales intradiarias.
     */
    private static final double TOLERANCIA_TEMPERATURA = 2.0;

    /**
     * Tolerancia fija para humedad relativa (%).
     * Reduce falsas alarmas por variaciones naturales intradiarias.
     */
    private static final double TOLERANCIA_HUMEDAD = 5.0;

    private final AlertaRepository alertaRepository;
    private final CultivoConUmbralesRepository cultivoRepo;
    private final WeatherService weatherService;
    private final Map<String, SeveridadStrategy> estrategias;
    private final RecomendacionService recomendacionService;
    private final SmsService smsService;

    public MotorAlertaService(AlertaRepository alertaRepository,
            CultivoConUmbralesRepository cultivoRepo,
            WeatherService weatherService,
            Map<String, SeveridadStrategy> estrategias,
            RecomendacionService recomendacionService,
            SmsService smsService) {
        this.alertaRepository     = alertaRepository;
        this.cultivoRepo          = cultivoRepo;
        this.estrategias          = estrategias;
        this.recomendacionService = recomendacionService;
        this.smsService           = smsService;
        this.weatherService       = weatherService;
    }

    /**
     * Punto de entrada del motor de alertas.
     * Invocado por AlertaScheduler a las 6AM y 6PM.
     * Para cada cultivo activo consulta el pronóstico de 16 días y
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
        List<DailyForecast> pronostico = weatherService.obtenerPronostico(
                cultivo.getLatitud(), cultivo.getLongitud(), 16);

        if (pronostico.isEmpty()) {
            log.warn("Sin pronóstico para cultivo id={} ({})", cultivo.getId(), cultivo.getCultivo());
            return;
        }

        // --- Lluvia: dos evaluaciones independientes ---
        // 1. Mínimo: acumulado 15 días vs umbral quincenal normalizado
        double lluviaAcumulada = pronostico.stream()
                .limit(15)
                .filter(d -> d.getLluviaMm() != null)
                .mapToDouble(DailyForecast::getLluviaMm)
                .sum();
        evaluarLluviaMinima(cultivo, lluviaAcumulada, LocalDate.now());

        // 2. Máximo: evento extremo diario > LLUVIA_EXTREMA_MM en los próximos 15 días
        evaluarLluviaExtrema(cultivo, pronostico);

        // --- Temperatura y humedad: hoy y mañana ---
        LocalDate hoy    = LocalDate.now();
        LocalDate manana = hoy.plusDays(1);

        pronostico.stream()
                .filter(dia -> {
                    LocalDate fechaDia = LocalDate.parse(dia.getFecha(),
                            DateTimeFormatter.ISO_LOCAL_DATE);
                    return fechaDia.equals(hoy) || fechaDia.equals(manana);
                })
                .forEach(dia -> evaluarDia(cultivo, dia));
    }

    /**
     * Evalúa si el acumulado de lluvia de los próximos 15 días
     * es insuficiente para el requerimiento hídrico del cultivo
     * en su etapa fenológica actual.
     * Tolerancia: max(10 mm, 10% del umbral quincenal).
     */
    private void evaluarLluviaMinima(CultivoConUmbralesDTO cultivo,
                                     double lluviaAcumulada, LocalDate fechaRef) {
        if (cultivo.getLluviaMinEfectiva() == null) return;

        SeveridadStrategy estrategia = estrategias.get(cultivo.getCategoria());
        double lluviaMinQuincenal = estrategia.normalizarLluvia(
                cultivo.getLluviaMinEfectiva(), cultivo.getDiasCosechaProm(),
                15, cultivo.getDiasRestantes());

        // Tolerancia proporcional: evita falsas alarmas en cultivos con umbrales
        // hídricos muy distintos (5 mm fijo sería muy estricto para arroceros
        // y muy permisivo para cultivos de secano).
        double tolerancia = Math.max(LLUVIA_TOLERANCIA_MIN, lluviaMinQuincenal * LLUVIA_TOLERANCIA_PCT);
        double umbralConTolerancia = lluviaMinQuincenal - tolerancia;

        verificarUmbral(cultivo, fechaRef, "LLUVIA_INSUFICIENTE",
                lluviaAcumulada, umbralConTolerancia, false);
    }

    /**
     * Evalúa si algún día de los próximos 15 tiene un evento de lluvia
     * extrema (> LLUVIA_EXTREMA_MM). No aplica tolerancia: el riesgo de
     * inundación o daño mecánico viene del pico diario, no del acumulado.
     */
    private void evaluarLluviaExtrema(CultivoConUmbralesDTO cultivo,
                                      List<DailyForecast> pronostico) {

        pronostico.stream()
                .limit(15)
                .filter(d -> d.getLluviaMm() != null
                          && d.getLluviaMm() >= SeveridadStrategy.LLUVIA_EXTREMA_MM)
                .forEach(d -> {
                    LocalDate fechaDia = LocalDate.parse(d.getFecha(),
                            DateTimeFormatter.ISO_LOCAL_DATE);
                    // Se pasa directamente al constructor de la alerta sin pasar
                    // por verificarUmbral porque la tolerancia mínima no aplica aquí.
                    SeveridadStrategy estrategia = estrategias.get(cultivo.getCategoria());
                    String severidad = estrategia.calcularSeveridadLluviaExtrema(
                            cultivo.getDiasRestantes(), cultivo.getDiasCosechaProm());
                    generarAlerta(cultivo, fechaDia, "LLUVIA_EXCESIVA",
                            d.getLluviaMm(), SeveridadStrategy.LLUVIA_EXTREMA_MM, severidad);
                });
    }

    /**
     * Evalúa temperatura y humedad para un día concreto del pronóstico.
     * Humedad: se evalúa humedadMin contra el umbral mínimo del cultivo
     * y humedadMax contra el umbral máximo, aplicando ±TOLERANCIA_HUMEDAD
     * para reducir falsas alarmas por variaciones naturales intradiarias.
     */
    private void evaluarDia(CultivoConUmbralesDTO cultivo, DailyForecast dia) {
        LocalDate fechaDia = LocalDate.parse(dia.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE);

        if (cultivo.getTempMaxEfectiva() != null && dia.getTempMax() != null)
            verificarUmbral(cultivo, fechaDia, "TEMPERATURA_ALTA",
                    dia.getTempMax(), 
                    cultivo.getTempMaxEfectiva() + TOLERANCIA_TEMPERATURA, true);

        if (cultivo.getTempMinEfectiva() != null && dia.getTempMin() != null)
            verificarUmbral(cultivo, fechaDia, "TEMPERATURA_BAJA",
                    dia.getTempMin(), 
                    cultivo.getTempMinEfectiva() - TOLERANCIA_TEMPERATURA, false);

        if (dia.getHumedadMax() != null && cultivo.getHumedadMaxEfectiva() != null)
            verificarUmbral(cultivo, fechaDia, "HUMEDAD_EXCESIVA",
                    dia.getHumedadMedia(),
                    cultivo.getHumedadMaxEfectiva() + TOLERANCIA_HUMEDAD, true);

        if (dia.getHumedadMin() != null && cultivo.getHumedadMinEfectiva() != null)
            verificarUmbral(cultivo, fechaDia, "HUMEDAD_INSUFICIENTE",
                    dia.getHumedadMedia(),
                    cultivo.getHumedadMinEfectiva() - TOLERANCIA_HUMEDAD, false);
    }

    /**
     * Comprueba si el valor detectado supera el umbral mínimo o máximo
     *
     * @param superaUmbral true = alerta cuando valorDetectado > umbral (excesivo)
     *                     false = alerta cuando valorDetectado < umbral (insuficiente)
     */
    private void verificarUmbral(CultivoConUmbralesDTO cultivo, LocalDate fechaDia,
            String tipoAlerta, double valorDetectado,
            double valorUmbral, boolean superaUmbral) {

        boolean disparar = superaUmbral
                ? valorDetectado > valorUmbral
                : valorDetectado < valorUmbral;
        if (!disparar) return;

        // Severidad base por etapa fenológica
        SeveridadStrategy estrategia = estrategias.get(cultivo.getCategoria());
        String severidad = estrategia.calcularSeveridad(
                cultivo.getDiasRestantes(), cultivo.getDiasCosechaProm());

        generarAlerta(cultivo, fechaDia, tipoAlerta, valorDetectado, valorUmbral, severidad);
    }

    private void generarAlerta(CultivoConUmbralesDTO cultivo, LocalDate fechaDia,
            String tipoAlerta, double valorDetectado, double valorUmbral, String severidad) {

        String descripcion = String.format(
                "Alerta %s en cultivo de %s (%s). Valor: %.2f — Umbral: %.2f",
                tipoAlerta, cultivo.getCultivo(), cultivo.getMunicipio(),
                valorDetectado, valorUmbral);

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
            log.info("Alerta generada: {} - {} - {}",
                    alerta.getTipoAlerta(), alerta.getSeveridad(), alerta.getFechaDiaPronostico());

            // 3. Envía SMS (fallo no interrumpe)
            if (cultivo.getTelefono() != null && !cultivo.getTelefono().isBlank()) {
                try {
                    String sms = String.format(
                            "[AgroCesar] Apreciado %s, se ha detectado %s para su cultivo de %s. Severidad: %s",
                            cultivo.getAgricultor(), alerta.getTipoAlerta(),
                            cultivo.getCultivo(), alerta.getSeveridad());
                    smsService.enviarSms(cultivo.getTelefono(), sms);
                } catch (Exception e) {
                    log.warn("SmsService falló: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
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