package com.example.deliveryfeecalculator.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WeatherData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationName;
    private String wmoCode;
    private double temperature;
    private double windSpeed;
    private String weatherPhenomenon;

    private LocalDateTime timestamp;
}
