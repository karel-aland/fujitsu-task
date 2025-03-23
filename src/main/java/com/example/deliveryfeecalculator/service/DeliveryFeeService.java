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

import com.example.deliveryfeecalculator.model.WeatherData;
import com.example.deliveryfeecalculator.repository.FeeRuleRepository;
import com.example.deliveryfeecalculator.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class DeliveryFeeService {

    private final WeatherDataRepository weatherDataRepository;
    @Autowired
    public DeliveryFeeService(WeatherDataRepository weatherDataRepository, FeeRuleRepository feeRuleRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    public double calculateDeliveryFee(String city, String vehicleType, String datetime) {
        vehicleType = vehicleType.trim().toLowerCase();
        
        System.out.println("Vehicle type: '" + vehicleType + "'");

        WeatherData weatherData;
        if (datetime != null && !datetime.isEmpty()) {
            weatherData = getWeatherDataByDatetime(city, datetime);
            if (weatherData == null) {
                System.out.println("Historical weather data unavailable for city: " + city + " at datetime: " + datetime);
                throw new RuntimeException("Historical weather data unavailable for city: " + city + " at datetime: " + datetime);
            }
            System.out.println("Historical weather data in use: " +
                    "Temp=" + weatherData.getTemperature() +
                    ", Wind=" + weatherData.getWindSpeed() +
                    ", Phenomenon=" + weatherData.getWeatherPhenomenon());
        } else {
            weatherData = getLatestWeatherData(city);
            if (weatherData == null) {
                System.out.println("Latest weather data unavailable for city: " + city);
                throw new RuntimeException("Latest weather data unavailable for city: " + city);
            }
            System.out.println("Latest weather data in use: " +
                    "Temp=" + weatherData.getTemperature() +
                    ", Wind=" + weatherData.getWindSpeed() +
                    ", Phenomenon=" + weatherData.getWeatherPhenomenon());
        }

        double rbf = getRegionalBaseFee(city, vehicleType);
        double atef = getTemperatureExtraFee(vehicleType, weatherData.getTemperature());
        double wsef = getWindSpeedExtraFee(vehicleType, weatherData.getWindSpeed());
        double wpef = getWeatherPhenomenonExtraFee(vehicleType, weatherData.getWeatherPhenomenon());

        double totalFee = rbf + atef + wsef + wpef;

        System.out.println("[DEBUG] RBF (base fee) = " + rbf);
        System.out.println("[DEBUG] ATEF (temperature fee) = " + atef);
        System.out.println("[DEBUG] WSEF (wind speed fee) = " + wsef);
        System.out.println("[DEBUG] WPEF (phenomenon fee) = " + wpef);
        System.out.println("[INFO] Total fee = " + totalFee);

        return totalFee;
    }

    private WeatherData getWeatherDataByDatetime(String city, String datetime) {
        String stationName = switch (city.toLowerCase().trim()) {
            case "tallinn" -> "Tallinn-Harku";
            case "tartu" -> "Tartu-Tõravere";
            case "pärnu" -> "Pärnu";
            default -> null;
        };

        if (stationName == null) {
            System.out.println("Invalid city name: " + city);
            return null;
        }

        System.out.println("Searching weather data from DB: '" + stationName + "'");

        if (datetime != null && !datetime.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime parsedDatetime = LocalDateTime.parse(datetime, formatter);
                System.out.println("Searching historical weather data from: " + parsedDatetime);

                return weatherDataRepository.findTopByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(stationName, parsedDatetime)
                        .orElse(null);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid datetime format: " + datetime);
                throw new IllegalArgumentException("Invalid datetime format. Use 'yyyy-MM-dd HH:mm:ss'");
            }
        }

        return weatherDataRepository.findTopByStationNameOrderByTimestampDesc(stationName.trim()).orElse(null);
    }

    private WeatherData getLatestWeatherData(String city) {
        return getWeatherDataByDatetime(city, null);
    }

    private double getRegionalBaseFee(String city, String vehicleType) {
        System.out.println("Checking base fee for: " + city + " and for vehicle: '" + vehicleType + "'");
        return switch (city.toLowerCase().trim()) {
            case "tallinn" -> switch (vehicleType) {
                case "car" -> 4.0;
                case "scooter" -> 3.5;
                case "bike" -> 3.0;
                default -> throw new IllegalArgumentException("ERROR: Invalid vehicle type: " + vehicleType);
            };
            case "tartu" -> switch (vehicleType) {
                case "car" -> 3.5;
                case "scooter" -> 3.0;
                case "bike" -> 2.5;
                default -> throw new IllegalArgumentException("ERROR: Invalid vehicle type: " + vehicleType);
            };
            case "pärnu" -> switch (vehicleType) {
                case "car" -> 3.0;
                case "scooter" -> 2.5;
                case "bike" -> 2.0;
                default -> throw new IllegalArgumentException("ERROR: Invalid vehicle type: " + vehicleType);
            };
            default -> throw new IllegalArgumentException("ERROR: Invalid city: " + city);
        };
    }

    private double getTemperatureExtraFee(String vehicleType, double temperature) {
        if (!vehicleType.equalsIgnoreCase("scooter") && !vehicleType.equalsIgnoreCase("bike")) {
            return 0.0;
        }
        double fee = 0.0;
        if (temperature < -10) {
            fee = 1.0;
        } else if (temperature < 0) {
            fee = 0.5;
        }
        System.out.println(" [DEBUG] Temperature fee: " + fee);
        return fee;
    }

    private double getWindSpeedExtraFee(String vehicleType, double windSpeed) {
        if (vehicleType.equalsIgnoreCase("bike")) {
            if (windSpeed > 20) {
                System.out.println("Usage of selected vehicle type is forbidden regarding wind speed" + windSpeed + " m/s");
                throw new RuntimeException("Usage of selected vehicle type is forbidden regarding bad weather conditions");
            }
            if (windSpeed >= 10) {
                System.out.println(" [DEBUG] Wind speed fee: 0.5");
                return 0.5;
            }
        }
        System.out.println(" [DEBUG] Wind speed fee: 0.0");
        return 0.0;
    }

    private double getWeatherPhenomenonExtraFee(String vehicleType, String phenomenon) {
        if (!vehicleType.equalsIgnoreCase("scooter") && !vehicleType.equalsIgnoreCase("bike")) {
            return 0.0;
        }
        if (phenomenon == null || phenomenon.trim().isEmpty()) return 0.0;

        String lowerPhenomenon = phenomenon.toLowerCase();
        double fee = 0.0;

        if (lowerPhenomenon.contains("glaze") || lowerPhenomenon.contains("hail") || lowerPhenomenon.contains("thunder")) {
            System.out.println("Usage of selected vehicle type is forbidden regarding bad weather conditions: " + phenomenon);
            throw new RuntimeException("Usage of selected vehicle type is forbidden regarding bad weather conditions");
        }
        if (lowerPhenomenon.contains("snow") || lowerPhenomenon.contains("sleet")) {
            fee = 1.0;
        } else if (lowerPhenomenon.contains("rain")) {
            fee = 0.5;
        }

        System.out.println("[DEBUG] Weather phenomenon fee: " + fee);
        return fee;
    }
}