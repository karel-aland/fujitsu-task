package com.example.deliveryfeecalculator.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "observations")
public class WeatherResponse {

    private List<WeatherStation> stations;

    @XmlElement(name = "station")
    public List<WeatherStation> getStations() {
        return stations;
    }

    public void setStations(List<WeatherStation> stations) {
        this.stations = stations;
    }

    public List<WeatherData> toWeatherDataList() {
        return stations.stream().map(WeatherStation::toWeatherData).collect(Collectors.toList());
    }
}
