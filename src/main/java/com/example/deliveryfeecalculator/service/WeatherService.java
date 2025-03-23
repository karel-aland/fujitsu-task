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
import com.example.deliveryfeecalculator.model.WeatherResponse;
import com.example.deliveryfeecalculator.repository.WeatherDataRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherService {

    private static final String API_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    private final WebClient webClient = WebClient.create();

    /**
     * Runs in every 15 minutes to fetch weather data from the API
     */
    @Scheduled(cron = "0 */15 * * * *") 
    public void fetchWeatherData() {
        System.out.println(" Fetching weather data...");

        String xmlResponse = webClient.get()
                .uri(API_URL)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (xmlResponse != null) {
            System.out.println("=== 🌍 ILMATEENISTUSE API RAW XML RESPONSE ===");
            System.out.println(xmlResponse); 
            System.out.println("=============================================");

            parseAndSaveWeatherData(xmlResponse);
        } else {
            System.out.println("Weather service API was invalid!");
        }
    }

    /**
     * Parses weather data and saves it to DB
     */
    @Transactional
    private void parseAndSaveWeatherData(String xml) {
        try {
            JAXBContext context = JAXBContext.newInstance(WeatherResponse.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            WeatherResponse response = (WeatherResponse) unmarshaller.unmarshal(new StringReader(xml));

            List<WeatherData> weatherDataList = response.toWeatherDataList();

            if (weatherDataList.isEmpty()) {
                System.out.println("No weather data in XML!");
            } else {
                weatherDataList.forEach(data -> {
                    System.out.println("[PARSE] Weather station: " + data.getStationName() +
                            ", Temp: " + data.getTemperature() +
                            ", Wind: " + data.getWindSpeed() +
                            ", Phenomenon: " + data.getWeatherPhenomenon());

                    // Add timestamp to each weather data
                    data.setTimestamp(LocalDateTime.now());
                });

                System.out.println("Saving in progress " + weatherDataList.size() + " weather data to DB...");

                weatherDataRepository.saveAll(weatherDataList);

                System.out.println("Weather data saved!");
            }
        } catch (JAXBException e) {
            System.err.println("XML parse unsuccessful: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Saving to DB was unsuccessful: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns latest data for weather station
     */
    public Optional<WeatherData> getLatestWeatherDataByStation(String stationName) {
        return weatherDataRepository.findTopByStationNameOrderByTimestampDesc(stationName);
    }
}
