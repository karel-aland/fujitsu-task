package com.example.deliveryfeecalculator.service;

import com.example.deliveryfeecalculator.model.WeatherData;
import com.example.deliveryfeecalculator.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WeatherDataService {

    private final WeatherDataRepository weatherDataRepository;

    @Autowired
    public WeatherDataService(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    public List<WeatherData> getAllWeatherData() {
        return weatherDataRepository.findAll();
    }

    public List<WeatherData> getWeatherDataByStation(String stationName) {
        return weatherDataRepository.findByStationNameOrderByTimestampDesc(stationName);
    }

    public Optional<WeatherData> getLatestWeatherDataByStation(String stationName) {
        return weatherDataRepository.findTopByStationNameOrderByTimestampDesc(stationName);
    }
}
