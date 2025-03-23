package com.example.deliveryfeecalculator.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "station")
public class WeatherStation {

    @XmlElement(name = "name", nillable = true)
    protected String name;

    @XmlElement(name = "wmocode", nillable = true)
    protected String wmoCode;

    @XmlElement(name = "airtemperature", nillable = true)
    protected Double airTemperature;

    @XmlElement(name = "windspeed", nillable = true)
    protected Double windSpeed;

    @XmlElement(name = "phenomenon", nillable = true)
    protected String weatherPhenomenon;

    @XmlElement(name = "visibility", nillable = true)
    protected Double visibility;

    @XmlElement(name = "relativehumidity", nillable = true)
    protected Integer relativeHumidity;

    public WeatherStation() {}

    public String getName() {
        return name;
    }

    public String getWmoCode() {
        return wmoCode;
    }

    public Double getAirTemperature() {
        return airTemperature;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public String getWeatherPhenomenon() {
        return weatherPhenomenon;
    }

    public Double getVisibility() {
        return visibility;
    }

    public Integer getRelativeHumidity() {
        return relativeHumidity;
    }

    public WeatherData toWeatherData() {
        WeatherData weatherData = new WeatherData();
        weatherData.setStationName(name != null ? name : "Unknown Station");
        weatherData.setWmoCode(wmoCode != null ? wmoCode : "N/A");
        weatherData.setTemperature(airTemperature != null ? airTemperature : 0.0);
        weatherData.setWindSpeed(windSpeed != null ? windSpeed : 0.0);
        weatherData.setWeatherPhenomenon(weatherPhenomenon != null ? weatherPhenomenon : "Unknown");
        weatherData.setTimestamp(java.time.LocalDateTime.now());
        return weatherData;
    }
}
