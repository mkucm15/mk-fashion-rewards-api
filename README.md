# Mickel Keevs Fashion Rewards API

This project is a Spring Boot-based RESTful API for a fashion store, Mickel Keevs, that manages customer reward points. The API calculates monthly and total rewards for customers based on their transaction history. It follows clean architectural practices and is designed for scalability, testability, and clarity.
>Note: "Mickel Keevs" is a fictional brand name I used solely for this technical assignment.
---

## Tools Used

- Java 17 (uses only Java 8 features as required)
- Spring Boot 3.x
- Spring Data JPA
- H2 In-Memory Database
- Maven
- JUnit 5
- SLF4J Logging

---

## Reward Calculation Policy

Reward points are awarded based on the amount of each transaction:

- 1 point for every dollar spent between $50 and $100 (inclusive)
- 2 points for every dollar spent above $100
- No points for spending below $50

Example:  
A $120 purchase = 1 × 50 + 2 × 20 = 90 points

---

## What This Project Does

- Clean layered architecture (Controller → Service → Repository)
- Use a real database via Spring Data JPA (H2 for simplicity)
- SQL-based schema and test data (via schema.sql and data.sql)
- Dynamic date filtering for custom or full reward range
- Breaks reward logic into modular service methods
- MockMvc and JUnit test coverage across controller and service layers
- Global exception handling with user-friendly responses
- Clear and structured REST API response

---

## API Usage

### Endpoint
```
GET /api/rewards/{customerId}
```

Optional query parameters:
- fromDate (yyyy-MM-dd): Start date for filtering
- toDate (yyyy-MM-dd): End date for filtering

### Example
```
GET http://localhost:8080/api/rewards/CUST001?fromDate=2024-04-01&toDate=2024-06-30
```

---

## Sample Response

```json
{
  "customerId": "CUST001",
  "customerName": "Murali Krishna",
  "fromDate": "2024-04-01",
  "toDate": "2024-06-30",
  "monthlyRewards": {
    "2024-04": 90,
    "2024-05": 40,
    "2024-06": 110
  },
  "totalRewards": 240
}
```

---

## How to Run and Test

```bash
mvn clean install
mvn spring-boot:run
```

API base URL: `http://localhost:8080`

H2 Console (for database view):  
`http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`

---

Run tests with:

```bash
mvn test
```

---

## Author

Author: MK (Murali Krishna), Backend Developer

This project was completed as part of a WebAPI Developer technical evaluation. It reflects production-ready structure with clarity and modularity.
