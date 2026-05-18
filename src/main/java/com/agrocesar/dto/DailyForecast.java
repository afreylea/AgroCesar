package com.agrocesar.dto;

public class DailyForecast {

    private String fecha;
    private Double tempMax;
    private Double tempMin;
    private Double lluviaMm;
    private Double humedadMax;
    private Double humedadMin;
    private Integer weatherCode;

    // Getters originales
    public String getFecha() { return fecha; }
    public Double getTempMax() { return tempMax; }
    public Double getTempMin() { return tempMin; }
    public Double getLluviaMm() { return lluviaMm; }
    public Double getHumedadMax() { return humedadMax; }
    public Double getHumedadMin() { return humedadMin; }
    public Integer getWeatherCode() { return weatherCode; }

    //ALIAS para la vista (dashboard.html usa estos nombres)
    public Double getLluvia() { return lluviaMm; }
    public Double getHumedad() { return humedadMax; }

    // Builder manual
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String fecha;
        private Double tempMax, tempMin, lluviaMm, humedadMax, humedadMin;
        private Integer weatherCode;

        public Builder fecha(String v) { this.fecha = v; return this; }
        public Builder tempMax(Double v) { this.tempMax = v; return this; }
        public Builder tempMin(Double v) { this.tempMin = v; return this; }
        public Builder lluviaMm(Double v) { this.lluviaMm = v; return this; }
        public Builder humedadMax(Double v) { this.humedadMax = v; return this; }
        public Builder humedadMin(Double v) { this.humedadMin = v; return this; }
        public Builder weatherCode(Integer v) { this.weatherCode = v; return this; }

        public DailyForecast build() {
            DailyForecast d = new DailyForecast();
            d.fecha = this.fecha;
            d.tempMax = this.tempMax;
            d.tempMin = this.tempMin;
            d.lluviaMm = this.lluviaMm;
            d.humedadMax = this.humedadMax;
            d.humedadMin = this.humedadMin;
            d.weatherCode = this.weatherCode;
            return d;
        }
    }
}