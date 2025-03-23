/**
 * Calculate delivery fee based on city, vehicle type, and weather conditions
 *
 * @param city City name (Tallinn, Tartu, Pärnu)
 * @param vehicleType Vehicle type (bike, scooter, car)
 * @param datetime (Optional) Date and time in format : "yyyy-MM-dd HH:mm:ss"
 * @return Calculated delivery fee
 * @throws RuntimeException if weather data is unavailable or invalid
 */

package com.example.deliveryfeecalculator.service;

import com.example.deliveryfeecalculator.model.FeeRule;
import com.example.deliveryfeecalculator.repository.FeeRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeeRuleService {

    private final FeeRuleRepository feeRuleRepository;

    @Autowired
    public FeeRuleService(FeeRuleRepository feeRuleRepository) {
        this.feeRuleRepository = feeRuleRepository;
    }

    public List<FeeRule> getAllFeeRules() {
        return feeRuleRepository.findAll();
    }

    public List<FeeRule> getFeeRulesByCity(String city) {
        List<FeeRule> feeRules = feeRuleRepository.findByCity(city);

        if (feeRules.isEmpty()) {
            // If no fee rules are found for the city, return default fee rules
            return getDefaultFeeRulesForCity(city);
        }

        return feeRules;
    }

    public Optional<FeeRule> getFeeRuleByCityAndVehicle(String city, String vehicleType) {
        return feeRuleRepository.findByCityAndVehicleType(city, vehicleType);
    }

    // Calculate base fee based on city, vehicle type, temperature, wind speed and datetime
    public Double calculateBaseFee(String city, String vehicleType, Double temperature, Double windSpeed, LocalDateTime datetime) {
        Optional<FeeRule> feeRuleOpt = feeRuleRepository.findByCityAndVehicleType(city, vehicleType);

        if (feeRuleOpt.isPresent()) {
            FeeRule feeRule = feeRuleOpt.get();
            double baseFee = feeRule.getBaseFee();

            // Add temperature extra fee
            if (temperature != null && temperature <= feeRule.getTempThreshold()) {
                baseFee += feeRule.getTempExtraFee();
            }

            // Add wind speed extra fee
            if (windSpeed != null && windSpeed >= feeRule.getWindSpeedThreshold()) {
                baseFee += feeRule.getWindSpeedExtraFee();
            }

            // Add weather extra fee
            if (feeRule.getWeatherCondition() != null && feeRule.getWeatherCondition().equals("snow")) {
                baseFee += feeRule.getWeatherExtraFee();
            }

            return baseFee;
        } else {
            throw new RuntimeException("Fee rule not found for city: " + city + " and vehicleType: " + vehicleType);
        }
    }

    // If datetime exists, then return only valid fee rules
    public List<FeeRule> getFeeRuleByCityAndVehicleWithDateTime(String city, String vehicleType, LocalDateTime datetime) {
        return feeRuleRepository.findValidFeeRules(city, vehicleType, datetime);
    }

    public FeeRule saveFeeRule(FeeRule feeRule) {
        return feeRuleRepository.save(feeRule);
    }

    public FeeRule updateFeeRule(Long id, FeeRule updatedRule) {
        if (!feeRuleRepository.existsById(id)) {
            throw new RuntimeException("FeeRule not found");
        }
        updatedRule.setId(id);
        return feeRuleRepository.save(updatedRule);
    }

    public void deleteFeeRule(Long id) {
        feeRuleRepository.deleteById(id);
    }

    // Method to return default fee rules for a city
    private List<FeeRule> getDefaultFeeRulesForCity(String city) {
        switch (city.toLowerCase()) {
            case "tallinn":
                return List.of(
                        new FeeRule("Tallinn", "Car", 4.0),
                        new FeeRule("Tallinn", "Scooter", 3.5),
                        new FeeRule("Tallinn", "Bike", 3.0)
                );
            case "tartu":
                return List.of(
                        new FeeRule("Tartu", "Car", 3.5),
                        new FeeRule("Tartu", "Scooter", 3.0),
                        new FeeRule("Tartu", "Bike", 2.5)
                );
            case "pärnu":
                return List.of(
                        new FeeRule("Pärnu", "Car", 3.0),
                        new FeeRule("Pärnu", "Scooter", 2.5),
                        new FeeRule("Pärnu", "Bike", 2.0)
                );
            default:
                return List.of(); // If the city is not found, return empty list
        }
    }
}
