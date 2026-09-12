# E-Kart-Microservices-Based-E-Commerce-Application
# Java 21 Event-Driven Microservices Ecosystem

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A production-grade, distributed microservices system engineered using **Java 21** and **Spring Boot 4.1.1**. This ecosystem demonstrates reactive API Gateway routing, dynamic service discovery, inter-service resilience, distributed tracing, and centralized persistence.

---

## 🏛️ System Architecture

                   +-------------------------+
                   |      Client / Postman    |
                   +------------+------------+
                                |
                                v
                   +-------------------------+
                   |   Spring Cloud Gateway  | <---> [ Redis (Rate Limiter) ]
                   |      (Port: 9090)       |
                   +------------+------------+
                                |
        +-----------------------+-----------------------+
        |                       |                       |
        v                       v                       v
+------------------+    +-------------------+    +------------------+
|  Order Service   |    |  Payment Service  |    | Product Service  |
|   (Port: 8082)   |    |   (Port: 8081)    |    |   (Port: 8080)   |
+--------+---------+    +---------+---------+    +--------+---------+
|                        |                       |
+------------------------+-----------------------+
|
v
+----------------------------+
| Service Registry (Eureka)  |
|        (Port: 8761)        |
+----------------------------+


---

## 🚀 Key Features

* **Java 21 Virtual Threads & Pattern Matching:** Built on Java 21 LTS utilizing updated language features and concurrent thread handling.
* **Reactive Edge Gateway:** Spring Cloud Gateway built on **Spring WebFlux and Reactor Netty**, decoupling blocking Servlet threads at the edge.
* **Distributed Rate Limiting:** Integrated Redis-backed Token Bucket algorithm (`RequestRateLimiter`) via reactive Lettuce drivers to prevent API flooding.
* **Dynamic Service Discovery:** Client-side load balancing (`lb://`) with **Netflix Eureka Server**.
* **Inter-Service Communication:** Declarative HTTP clients using **Spring Cloud OpenFeign** and `RestTemplate` with Jackson enum & DTO mappings.
* **Database & Persistence:** Relational entity modeling using **Spring Data JPA** and **MySQL**.
* **Observability & Distributed Tracing:** Cross-service correlation IDs and span propagation enabled through **Micrometer Tracing** and **Zipkin**.

---

## 🛠️ Tech Stack & Dependencies

* **Language:** Java 21
* **Framework:** Spring Boot `4.1.1`
* **Cloud Infrastructure:** Spring Cloud `2023.0.0`
* **Edge Routing:** Spring Cloud Gateway (Reactive WebFlux)
* **Service Discovery:** Spring Cloud Netflix Eureka
* **Caching & Throttling:** Redis Server, Spring Data Reactive Redis
* **Database:** MySQL 8.x, Hibernate / JPA
* **Tools & Utilities:** Lombok, Apache Maven

---

## 📦 Project Structure

```text
microservices-order-ecosystem/
├── API-GATEWAY/              # Reactive Spring Cloud Gateway & Redis Rate Limiter
├── ServiceRegistry/          # Eureka Discovery Server
├── OrderService/             # Core Order Management & Orchestration Service
├── PaymentService/           # Transaction Processing Service
├── ProductService/           # Inventory & Stock Verification Service
└── README.md                 # System Documentation
⚙️ Prerequisites
Ensure you have the following installed locally before running the application:

JDK 21 or higher

Apache Maven 3.8+

MySQL 8.0+ running on localhost:3306

Redis Server running on localhost:6379

🏃 Getting Started
1. Database Setup
Create the required MySQL database instances:

SQL
CREATE DATABASE orderdb;
CREATE DATABASE paymentdb;
CREATE DATABASE productdb;
2. Microservice Startup Order
Services must be launched in the following sequence to allow registry discovery and edge routing initialization:

Service Registry (Eureka Server)

Bash
cd ServiceRegistry
mvn spring-boot:run
Dashboard available at: http://localhost:8761

Core Domain Services (Run in separate terminal windows)

Bash
# Product Service (Port: 8080)
cd ProductService && mvn spring-boot:run

# Payment Service (Port: 8081)
cd PaymentService && mvn spring-boot:run

# Order Service (Port: 8082)
cd OrderService && mvn spring-boot:run
API Gateway

Bash
cd API-GATEWAY
mvn spring-boot:run
Gateway active at: http://localhost:9090

🚦 API Reference & Verification
All client calls should be routed through the API Gateway on port 9090.

Fetch Order Details
HTTP
GET http://localhost:9090/order/203
Accept: application/json
Sample Response (200 OK):

JSON
{
  "orderId": 203,
  "orderDate": "2026-09-12T04:34:26.194Z",
  "orderStatus": "PLACED",
  "amount": 1500,
  "productDetails": {
    "productId": 101,
    "productName": "Wireless Headphones",
    "price": 1500,
    "quantity": 1
  },
  "paymentDetails": {
    "paymentId": 501,
    "paymentStatus": "SUCCESS",
    "paymentMode": "CREDIT_CARD",
    "paymentDate": "2026-09-12T04:34:26.200Z"
  }
}
🛡️ Rate Limiting & Resiliency Configuration
Redis Rate Limiting is enforced at the Gateway using the Token Bucket algorithm:

YAML
# API-GATEWAY application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: ORDER-SERVICE
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/order/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                redis-rate-limiter.requestedTokens: 1
