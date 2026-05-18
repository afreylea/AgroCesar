package com.agrocesar.dto;

public class DailyForecast {

    private String fecha;
    private double tempMax;
    private double tempMin;
    private double lluviaMm;
    private double humedadMax;
    private double humedadMin;
    private int weatherCode;

    // Builder manual
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String fecha;
        private double tempMax, tempMin, lluviaMm, humedadMax, humedadMin;
        private int weatherCode;

        public Builder fecha(String v) { this.fecha = v; return this; }
        public Builder tempMax(double v) { this.tempMax = v; return this; }
        public Builder tempMin(double v) { this.tempMin = v; return this; }
        public Builder lluviaMm(double v) { this.lluviaMm = v; return this; }
        public Builder humedadMax(double v) { this.humedadMax = v; return this; }
        public Builder humedadMin(double v) { this.humedadMin = v; return this; }
        public Builder weatherCode(int v) { this.weatherCode = v; return this; }

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

    public String getFecha() { return fecha; }
    public double getTempMax() { return tempMax; }
    public double getTempMin() { return tempMin; }
    public double getLluviaMm() { return lluviaMm; }
    public double getHumedadMax() { return humedadMax; }
    public double getHumedadMin() { return humedadMin; }
    public int getWeatherCode() { return weatherCode; }
}