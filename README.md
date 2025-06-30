# Mickel Keevs Fashion Rewards API
This project is a Spring Boot-based RESTful API for a fashion store, **Mickel Keevs**, that manages customer reward points. The API computes monthly and total reward points based on transaction history and follows clean architectural principles, testability, and scalability.
> Note: "Mickel Keevs" is a fictional brand name I used solely for the purpose of this technical assignment.
---

## Tools Used

- Java 11 (but only Java 8 features used as mentioned)
- Spring Boot 3.x
- Maven
- JUnit 5
- SLF4J Logging

---

## Reward Calculation Policy

Reward points are earned per transaction:

- 1 point for every dollar spent between $50 and $100 (inclusive)
- 2 points for every dollar spent above $100
- No points for spending below $50

**Example:**  
$120 purchase → 1 × 50 + 2 × 20 = **90 points**

---

## What This Project Does

- Clean layered architecture (Controller → Service → Repository)
- Pluggable reward strategy via `RewardPolicy`
- In-memory customer transaction data
- Dynamic date-based filtering (monthly or custom range)
- Monthly breakdown and total rewards
- Global exception handling
- Comprehensive test coverage with real-world scenarios
- Lightweight logging for debugging and traceability
- Fully DB-ready structure (can replace in-memory with JPA)

---

## API Usage

### Endpoint
```
GET /api/rewards/{customerId}
```

We can optionally provide query parameters:
- fromDate (yyyy-MM-dd): Start date to filter rewards (e.g., 2024-04-01)
- toDate (yyyy-MM-dd): End date to filter rewards (e.g., 2024-06-30)

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
  "totalRewards": 240,
  "transactions": [
    {
      "transactionId": "TXN1001",
      "amount": 120.0,
      "transactionDate": "2024-04-15"
    }
  ]
}
```

---

## How to Run and Test

```bash
mvn clean install
mvn spring-boot:run
```

API base URL: `http://localhost:8080`

---

Run tests with:

```bash
mvn test
```

Covers:
- Valid customer with rewards
- Invalid customer exception
- Customer with no rewards in range
- One-month summary
- Full summary without filters

---

## Author

Author: MK (Murali Krishna), Backend Developer

This project was submitted as part of a WebAPI Developer technical evaluation. It emphasizes simplicity, clarity, scalability, and production readiness.