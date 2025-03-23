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

import com.example.deliveryfeecalculator.service.DeliveryFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/delivery-fee")
public class DeliveryFeeController {

    private static final Logger logger = Logger.getLogger(DeliveryFeeController.class.getName());

    private final DeliveryFeeService deliveryFeeService;

    @Autowired
    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    @GetMapping
    public ResponseEntity<?> getDeliveryFee(
            @RequestParam("city") String city,
            @RequestParam("vehicleType") String vehicleType,
            @RequestParam(value = "datetime", required = false) String datetime) {

        // Decode and fix datetime format
        if (datetime != null) {
            datetime = URLDecoder.decode(datetime, StandardCharsets.UTF_8); // Decode URL
            datetime = datetime.replace("T", " "); // Replace 'T' with space

            // Check if datetime is in correct format
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime parsedDatetime = LocalDateTime.parse(datetime, formatter);
                logger.info("Correct datetime format: " + parsedDatetime);
            } catch (Exception e) {
                logger.warning("Invalid datetime format: " + datetime);
                return ResponseEntity.badRequest().body("ERROR: Invalid datetime format. Use 'yyyy-MM-dd HH:mm:ss'");
            }
        }

        logger.info("Request accepted: city=" + city + ", vehicleType=" + vehicleType + ", datetime=" + datetime);

        try {
            double fee = deliveryFeeService.calculateDeliveryFee(city, vehicleType, datetime);
            logger.info("Delivery fee calculated: " + fee);
            return ResponseEntity.ok(fee);
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid input: " + e.getMessage());
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal Server Error: " + e.getMessage());
        }
    }
}
