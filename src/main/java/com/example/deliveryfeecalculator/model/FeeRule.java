package com.example.deliveryfeecalculator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor  
@AllArgsConstructor 
public class FeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;        
    private String vehicleType; 
    private double baseFee;     // RBF

    private Double tempThreshold; // Temperature fee
    private Double tempExtraFee;

    private Double windSpeedThreshold; // Wind speed fee
    private Double windSpeedExtraFee;

    private String weatherCondition; // Weather condition fee
    private Double weatherExtraFee;

    private LocalDateTime validFrom; 
    private LocalDateTime validTo;   

    // Constructor with three main parameters
    public FeeRule(String city, String vehicleType, double baseFee) {
        this.city = city;
        this.vehicleType = vehicleType;
        this.baseFee = baseFee;
    }
}
