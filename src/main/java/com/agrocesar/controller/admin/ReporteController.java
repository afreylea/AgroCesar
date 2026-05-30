package com.agrocesar.controller.admin;

import com.agrocesar.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public String reportes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            Model model) {

        if (fechaDesde == null)
            fechaDesde = LocalDate.now().minusMonths(1);
        if (fechaHasta == null)
            fechaHasta = LocalDate.now();

        model.addAttribute("alertas", reporteService.alertasPorPeriodo(fechaDesde, fechaHasta));
        model.addAttribute("cultivosAfectados", reporteService.cultivosMasAfectados(fechaDesde, fechaHasta));
        model.addAttribute("totalActivas", reporteService.totalAlertasActivas(fechaDesde, fechaHasta));
        model.addAttribute("totalCriticas", reporteService.totalAlertasCriticas(fechaDesde, fechaHasta));
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "admin/reportes";
    }
}