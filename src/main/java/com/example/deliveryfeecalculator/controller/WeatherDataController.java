/**
 * Calculate delivery fee based on city, vehicle type, and weather conditions
 *
 * @param city City name (Tallinn, Tartu, Pärnu)
 * @param vehicleType Vehicle type (bike, scooter, car)
 * @param datetime (Optional) Date and time in format : "yyyy-MM-dd HH:mm:ss"
 * @return Calculated delivery fee
 * @throws RuntimeException if weather data is unavailable or invalid
 */

package com.example.deliveryfeecalculator.controller;

import com.example.deliveryfeecalculator.model.WeatherData;
import com.example.deliveryfeecalculator.service.WeatherDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/weather-data")
public class WeatherDataController {

    private final WeatherDataService weatherDataService;

    @Autowired
    public WeatherDataController(WeatherDataService weatherDataService) {
        this.weatherDataService = weatherDataService;
    }

    // Searches for all weather data
    @GetMapping
    public ResponseEntity<List<WeatherData>> getAllWeatherData() {
        return ResponseEntity.ok(weatherDataService.getAllWeatherData());
    }

    // Searches weather data for specific weather station
    @GetMapping("/{stationName}")
    public ResponseEntity<List<WeatherData>> getWeatherDataByStation(@PathVariable String stationName) {
        List<WeatherData> weatherData = weatherDataService.getWeatherDataByStation(stationName);
        if (weatherData.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(weatherData);
    }

    // Searches for latest weather data for specific weather station
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestWeatherData(@RequestParam String stationName) {
        Optional<WeatherData> latestData = weatherDataService.getLatestWeatherDataByStation(stationName);
        return latestData.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
