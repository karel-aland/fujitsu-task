package com.example.deliveryfeecalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.deliveryfeecalculator.model.WeatherData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    // Searches for the latest weather data or a specific time stamp
    Optional<WeatherData> findTopByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(String stationName, LocalDateTime timestamp);

    // Searches for the latest weather data for specific city
    Optional<WeatherData> findTopByStationNameOrderByTimestampDesc(String stationName);

    // Searches for all weather data for specific city
    List<WeatherData> findByStationNameOrderByTimestampDesc(String stationName);
}



