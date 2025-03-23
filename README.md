# 📄 Delivery Fee Calculator API Documentation

This project is a **Spring Boot REST API** that calculates delivery fees based on weather conditions and vehicle type.

---

## 🚀 API Endpoints

| Method | URL                                      | Description |
|--------|------------------------------------------|-------------|
| `GET`  | `/api/delivery-fee`                     | Calculates the delivery fee |
| `GET`  | `/api/weather-data/latest`              | Retrieves the latest weather data |
| `GET`  | `/api/weather-data/history?date=...`    | Retrieves historical weather data |
| `POST` | `/api/fee-rules`                        | Adds a new business rule |

---

## Fujitsu Delivery Fee Calculator application Postman API Collection is located in the main folder.

**Usage in Postman:**
1. Open **Postman**
2. Go to **File → Import**
3. Select `Fujitsu_delivery_fee_API_tests.postman_collection.json`
4. API requests will appear under **Collections**

---

## 📝 API Endpoints Documentation

### 🔹 **GET /api/delivery-fee**
**Description:**  
Calculates the delivery fee based on the city, vehicle type, and weather conditions.

**Example request:**  
```sh
GET http://localhost:8081/api/delivery-fee?city=Tartu&vehicleType=bike

Query parameters:

Parameter	Description	Example
city	City	Tartu
vehicleType	Vehicle Type	bike
datetime (optional)	Historical datetime	2024-03-10 12:00:00
Response (200 OK):

{
    "deliveryFee": 4.5
}

Error (400 Bad Request):
{
    "error": "Invalid datetime format. Use 'yyyy-MM-dd HH:mm:ss'"
}

GET /api/weather-data/latest
Description:
Retrieves the latest available weather data for a specific weather station.

Example request:

GET http://localhost:8081/api/weather-data/latest?stationName=Tartu-Tõravere
Query parameters:

Parameter	Description	Example
stationName	Weather station name	Tartu-Tõravere
Response (200 OK):

{
    "stationName": "Tartu-Tõravere",
    "temperature": -3.8,
    "windSpeed": 0.5,
    "weatherPhenomenon": "Clear",
    "timestamp": "2025-03-22 06:09:18"
}

Error (404 Not Found):
{
    "error": "Weather data not found for station: Tartu-Tõravere"
}
POST /api/fee-rules
Description:
Adds a new business rule for calculating delivery fees.

Example request:

POST http://localhost:8081/api/fee-rules
Request Body:

{
    "city": "Tartu",
    "vehicleType": "bike",
    "baseFee": 2.5,
    "tempThreshold": -10,
    "tempExtraFee": 1.0,
    "windSpeedThreshold": 10,
    "windSpeedExtraFee": 0.5,
    "weatherCondition": "snow",
    "weatherExtraFee": 1.0
}
Response (201 Created):
{
    "message": "Fee rule added successfully"
}

Error (400 Bad Request):
{
    "error": "Invalid request data"
}

Checking Data in H2 Console
The application uses an H2 database, which is an in-memory database by default. You can inspect the database using the H2 Console.

Open H2 Console
1. Open terminal
2. Type following command cd "C:\path\to\your\folder\delivery-fee-calculator"
3. Type "mvn spring-boot:run"

Open H2 Console in your browser:
http://localhost:8081/h2-console

Use the following credentials:
JDBC URL: jdbc:h2:mem:weatherdb
Username: sa
Password: (leave empty)

Check Weather Data
Run the following SQL query to check stored weather data:
SELECT * FROM WEATHER_DATA;

Check Delivery Fee Rules
To see all configured delivery fee rules, use:
SELECT * FROM FEE_RULE;

Add Historical Weather Data
If you need to insert a historical weather record manually:
INSERT INTO WEATHER_DATA (station_name, timestamp, temperature, wind_speed, weather_phenomenon) 
VALUES ('Tartu-Tõravere', '2024-03-10 12:00:00', -2.5, 5.2, 'snow');

properties

spring.datasource.url=jdbc:h2:file:./data/delivery_fee_db
spring.jpa.hibernate.ddl-auto=update


Testing
The project includes JUnit and Mockito tests.

Run Tests with Maven

1. Open terminal
2. Type following command cd "C:\path\to\your\folder\delivery-fee-calculator"
3. Type "mvn test"

If all tests pass, you will see output similar to this:

[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS

Project Structure & Clean Code Principles
This project follows clean architecture principles to ensure maintainability and readability.

📁 Project Structure

/delivery-fee-calculator
│── src/main/java/com/example/deliveryfeecalculator
│   ├── controller/       # REST API Controllers
│   ├── model/            # Entity models
│   ├── repository/       # Database access layer
│   ├── service/          # Business logic
│── src/test/java/com/example/deliveryfeecalculator
│   ├── tests/            # Unit and integration tests
│── Fujitsu_delivery_fee_API_tests/              # API testing collection
│── README.md             # Documentation
│── pom.xml               # Maven dependencies

Clean Code Principles Followed
✅ Layered Architecture: Separation of concerns between controllers, services, and repositories.
✅ Human-Readable Code: Proper naming conventions and structured logic.
✅ Error Handling: Clear exceptions and error messages.
✅ Code Documentation: Public methods include meaningful JavaDoc comments.
✅ Test Coverage: Unit tests for business logic and API endpoints.