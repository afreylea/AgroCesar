package com.agrocesar.service;

import org.springframework.stereotype.Service;

/**
 * SiembraOptimaService analiza el pronostico climatico de los proximos 7 dias
 * para un cultivo registrado y determina si la ventana actual es optima para
 * sembrar, usando los umbrales efectivos del catalogo y generando una guia
 * de accion preventiva via Groq.
 *
 * Patron: mismo que RecomendacionService — retorna null si Groq falla,
 * nunca interrumpe el flujo principal (RNF05).
 */

@Service
public class SiembraOptimaService {
    
}
