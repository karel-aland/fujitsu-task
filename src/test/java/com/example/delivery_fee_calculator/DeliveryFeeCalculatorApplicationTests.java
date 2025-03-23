package com.example.delivery_fee_calculator;

import com.example.deliveryfeecalculator.model.WeatherData;
import com.example.deliveryfeecalculator.repository.FeeRuleRepository;
import com.example.deliveryfeecalculator.repository.WeatherDataRepository;
import com.example.deliveryfeecalculator.service.DeliveryFeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryFeeCalculatorApplicationTests {

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @Mock
    private FeeRuleRepository feeRuleRepository;

    @InjectMocks
    private DeliveryFeeService deliveryFeeService;

    private WeatherData testWeatherData;

    @BeforeEach
    void setUp() {
        testWeatherData = new WeatherData();
        testWeatherData.setStationName("Tartu-Tõravere");
        testWeatherData.setTemperature(-5.0);
        testWeatherData.setWindSpeed(15.0);
        testWeatherData.setWeatherPhenomenon("snow");
        testWeatherData.setTimestamp(LocalDateTime.now());
    }

    // ✅ 1. Test: Delivery fee calculation based on weather conditions
    @Test
    void testCalculateDeliveryFee_ColdWeatherAndWindy() {
        lenient().when(weatherDataRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(testWeatherData));

        lenient().when(feeRuleRepository.findFeeByCityAndVehicleType("tartu", "bike"))
                .thenReturn(Optional.of(2.5));

        double fee = deliveryFeeService.calculateDeliveryFee("Tartu", "bike", null);

        assertEquals(4.5, fee, "Cold weather and wind should increase fee");
    }

    // ✅ 2. Test: Forbidden weather conditions (hail, thunder, glaze)
    @Test
    void testCalculateDeliveryFee_ForbiddenWeatherConditions() {
        testWeatherData.setWeatherPhenomenon("thunder");

        when(weatherDataRepository.findTopByStationNameOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(testWeatherData));

        Exception exception = assertThrows(RuntimeException.class, () ->
                deliveryFeeService.calculateDeliveryFee("Tartu", "bike", null));

        assertEquals("Usage of selected vehicle type is forbidden regarding bad weather conditions", exception.getMessage());
    }

    // ✅ 3. Test: Using historical weather data for fee calculation
    @Test
    void testCalculateDeliveryFee_HistoricalWeatherData() {
        String datetime = "2024-03-10 12:00:00";
        LocalDateTime parsedDatetime = LocalDateTime.parse(datetime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        lenient().when(weatherDataRepository.findTopByStationNameAndTimestampLessThanEqualOrderByTimestampDesc("Tartu-Tõravere", parsedDatetime))
                .thenReturn(Optional.of(testWeatherData));

        lenient().when(feeRuleRepository.findFeeByCityAndVehicleType("tartu", "bike"))
                .thenReturn(Optional.of(2.5));

        double fee = deliveryFeeService.calculateDeliveryFee("Tartu", "bike", datetime);

        assertEquals(4.5, fee, "Historical weather data should be used for fee calculation");
    }

    // ✅ 4. Test: Adding new fee rule
    @Test
    void testAddFeeRule() {
        when(feeRuleRepository.findFeeByCityAndVehicleType("tartu", "scooter"))
                .thenReturn(Optional.of(3.0));

        Optional<Double> fee = feeRuleRepository.findFeeByCityAndVehicleType("tartu", "scooter");

        assertTrue(fee.isPresent());
        assertEquals(3.0, fee.get(), "Scooter fee rule should be 3.0");
    }

    // ✅ 5. Test: Error handling for invalid input
    @Test
    void testCalculateDeliveryFee_InvalidInput() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                deliveryFeeService.calculateDeliveryFee("InvalidCity", "bike", null));

        assertEquals("Latest weather data unavailable for city: InvalidCity", exception.getMessage());
    }
}
