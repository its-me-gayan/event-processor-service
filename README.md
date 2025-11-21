# Event Processor Service

A Kotlin-based Spring Boot microservice designed to **ingest user events**, **enrich them**, and **reliably publish them** to a message broker using the **Inbox–Outbox pattern**.
This project demonstrates strong backend engineering practices including transactional messaging, asynchronous processing, error-handling, and resilient architecture.

---

## 📌 **Features**

### ✅ Event Ingestion API

* Accepts raw user events via REST (`POST /api/v1/events`).
* Validates and stores events in the **Inbox** table for reliable processing.

### ✅ Event Enrichment

* Each incoming event is enriched with additional metadata:

  * Country enrichment
  * Timestamping
  * Event transformation

### ✅ Reliable Messaging (Inbox–Outbox Pattern)

* Inbox → Process → Outbox → Publish
* Guarantees **no message loss** and **exactly-once publishing behavior**, even if services crash.

### ✅ Kafka Integration

* Publishes enriched events to a Kafka topic.
* Consumer implementation included for demonstration.

### ✅ Liquibase Database Versioning

* Automated schema creation:

  * `inbox_event` table
  * `outbox_event` table

### ✅ Generic Response Builder

* All API responses use a consistent envelope:

  ```json
  {
    "success": true/false,
    "message": "...",
    "data": {...}
  }
  ```

### ✅ Global Error Handling

* Centralized exception handling with custom error messages.

---

## 🔧 **Tech Stack**

| Layer            | Technology                |
| ---------------- | ------------------------- |
| Language         | Kotlin                    |
| Framework        | Spring Boot, Spring WebFlux |
| Messaging        | Apache Kafka              |
| Database         | PostgreSQL (configurable) |
| Schema Migration | Liquibase                 |
| Build Tool       | Gradle (Kotlin DSL)       |
| Testing          | JUnit 5                   |

---

## 📂 **Project Structure**

```
src/main/kotlin/com/example/eps
 ├── config/                 # Kafka, WebClient, OpenAPI config
 ├── controller/             # REST controllers
 ├── service/                # Business logic
 ├── repository/             # JPA repositories
 ├── model/
 │    ├── dto/               # API DTOs & response objects
 │    └── entity/            # Inbox/Outbox entities
 ├── message/
 │    ├── consumer/          # Kafka consumer
 │    └── publisher/         # Kafka publisher
 ├── exception/              # Exception + Global handler
 └── util/                   # Helpers & utilities
```

---

## 🚀 **How It Works (Flow)**

### 1️⃣ **Client sends a user event**

```
POST /api/v1/events
{
  "userId": "123",
  "payload": {...}
}
```

### 2️⃣ **Service stores the event in Inbox**

* Status: `RECEIVED`

### 3️⃣ **Inbox Processor picks it up**

* Enriches data
* Writes enriched version into **Outbox** table

### 4️⃣ **Outbox Publisher sends message to Kafka**

### 5️⃣ **Kafka consumer receives & logs/handles event**

This pattern gives:

* No duplicate messages
* No lost events
* Full crash recovery

---

## 🗄️ **Database Schema (Simplified)**

### Inbox Table

| column  | description              |
| ------- | ------------------------ |
| id      | unique pk                |
| payload | original event           |
| status  | PENDING/PROCESSED/FAILED |

### Outbox Table

| column  | description    |
| ------- | -------------- |
| id      | unique pk      |
| payload | enriched event |
| status  | READY/SENT     |

---

## ▶️ **Running the Application**

### **Start the full stack**

```
docker compose up --build
```

Service will start on:

```
http://localhost:8080
```

---


## 📘 **API Documentation**

OpenAPI / Swagger UI available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🧱 **Improvements (Future Work)**

* Add retries & dead-letter queue for Kafka
* Add distributed tracing
* Add metrics (Micrometer + Prometheus)

---

## 👨‍💻 Author

Prepared by **Gayan Sanjeewa** as part of a technical assessment.
Feel free to reach out for improvements or deeper architectural discussion.

---

If you need a **short version**, **corporate version**, or **ATS-optimized version**, I can generate it.

```​:contentReference[oaicite:0]{index=0}​
```
