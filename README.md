# Marvel Hospitality - Room Reservation Service

## Project Overview

The `room-reservation-service` is a core microservice for Marvel Hospitality's IT infrastructure, responsible for managing the confirmation and lifecycle of room reservations. It is built using **Spring Boot** and follows a microservice architecture, incorporating RESTful APIs, external service integration, and event-driven consumption for payment processing.

This service implements the following key functionalities:

1.  **Room Reservation Confirmation API**: A REST endpoint to confirm a room reservation based on the mode of payment.
2.  **Event-Driven Payment Processing**: Consumption of payment updates for bank transfers to confirm pending reservations.
3.  **Automatic Cancellation**: A scheduled task to automatically cancel bank transfer reservations if payment is not received within 2 days.

## Technology Stack

*   **Framework**: Spring Boot 4.x
*   **Language**: Java 21
*   **Build Tool**: Maven
*   **Persistence**: Spring Data JPA
*   **Database**: H2 (In-memory for testing), PostgreSQL (Recommended for production)
*   **Messaging**: Spring Kafka (Simulated for event consumption)
*   **Testing**: JUnit 5, Mockito

## Prerequisites

*   Java Development Kit (JDK) 17 or later
*   Maven 3.6 or later

## Getting Started

### 1. Build the Project

Navigate to the project root directory (`room-reservation-service`) and run the following command to build the application and run all tests:

```bash
mvn clean verify
```

### 2. Run the Application

The application can be run directly using the Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

The service will start on the default port `8080`.

## API Endpoint

The primary functionality is exposed via a single REST endpoint for confirming reservations.

### `POST /api/v1/reservations/confirm`

Confirms a room reservation based on the provided details and payment mode.

| Attribute | Description | Required | Example |
| :--- | :--- | :--- | :--- |
| `customerName` | Name of the customer. | Yes | `"Tony Stark"` |
| `roomNumber` | The room number to be reserved. | Yes | `"A101"` |
| `startDate` | Reservation start date (YYYY-MM-DD). | Yes | `"2025-12-15"` |
| `endDate` | Reservation end date (YYYY-MM-DD). | Yes | `"2025-12-18"` |
| `roomSegment` | Size of the room. | Yes | `"LARGE"` |
| `paymentMode` | Mode of payment. | Yes | `"CASH"` |
| `paymentReference` | Unique reference for the payment. | Conditional | `"P4145478"` |

**Validations:**
*   Reservation duration cannot exceed 30 days.
*   `paymentReference` is mandatory for `CREDIT_CARD` and `BANK_TRANSFER`.

**Response Body:**

| Attribute | Description | Example |
| :--- | :--- | :--- |
| `reservationId` | The unique ID of the reservation. | `1` |
| `reservationStatus` | The final status of the reservation. | `"CONFIRMED"` |

**Payment Mode Logic:**

| Payment Mode | Logic | Final Status |
| :--- | :--- | :--- |
| **CASH** | Confirmed immediately. | `CONFIRMED` |
| **CREDIT_CARD** | Calls external `credit-card-payment-service` (simulated). Confirmed only if payment is successful, otherwise throws an error. | `CONFIRMED` or throws `PaymentException` |
| **BANK_TRANSFER** | Confirmed asynchronously via Kafka event. | `PENDING_PAYMENT` |

## Event-Driven Processing

The service includes a Kafka consumer (`PaymentUpdateListener`) that listens to the `bank-transfer-payment-update` topic.

The `transactionDescription` in the Kafka event is parsed to extract the 8-character payment reference, which is then used to find and update the corresponding reservation from `PENDING_PAYMENT` to `CONFIRMED`.

**Transaction Description Format:**
`<E2E unique id(10 character)> <reservationId (8 characters)>`
*Example: `1401541457 P4145478`*

## Testing

The project includes a comprehensive suite of tests to ensure reliability:

*   **Unit Tests**: Located in `src/test/java/.../service/ReservationServiceImplTest.java`, covering all business logic, payment mode flows, and the refined `confirmBankTransferPayment` parsing logic.
*   **Integration Tests**: Located in `src/test/java/.../controller/ReservationControllerIntegrationTest.java` and `src/test/java/.../repository/ReservationRepositoryIntegrationTest.java`. These tests use **Spring Boot's testing framework** and an **in-memory H2 database** for persistence testing.


## Domain Model

Key entities and enums used in the application:

| Component | Description |
| :--- | :--- |
| `Reservation` | JPA Entity representing a room reservation. |
| `ReservationRequest` | DTO for the incoming API request. |
| `ReservationResponse` | DTO for the outgoing API response. |
| `PaymentMode` | Enum: `CASH`, `CREDIT_CARD`, `BANK_TRANSFER`. |
| `ReservationStatus` | Enum: `PENDING_PAYMENT`, `CONFIRMED`, `CANCELLED`. |
| `RoomSegment` | Enum: `SMALL`, `MEDIUM`, `LARGE`, `EXTRA_LARGE`. |

