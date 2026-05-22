package com.agrocesar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ForecastResponse {

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("daily")
    private DailyData daily;

    public DailyData getDaily() { return daily; }
    public void setDaily(DailyData daily) { this.daily = daily; }

    public static class DailyData {

        @JsonProperty("time")
        private List<String> time;

        @JsonProperty("temperature_2m_max")
        private List<Double> temperatureMax;

        @JsonProperty("temperature_2m_min")
        private List<Double> temperatureMin;

        @JsonProperty("precipitation_sum")
        private List<Double> precipitationSum;

        @JsonProperty("relative_humidity_2m_max")
        private List<Double> humidityMax;

        @JsonProperty("relative_humidity_2m_min")
        private List<Double> humidityMin;

        @JsonProperty("weathercode")
        private List<Integer> weatherCode;


        public List<String> getTime() { return time; }
        public List<Double> getTemperatureMax() { return temperatureMax; }
        public List<Double> getTemperatureMin() { return temperatureMin; }
        public List<Double> getPrecipitationSum() { return precipitationSum; }
        public List<Double> getHumidityMax() { return humidityMax; }
        public List<Double> getHumidityMin() { return humidityMin; }
        public List<Integer> getWeatherCode() { return weatherCode; }
    }
}