# 🛒 E-Commerce REST API

A RESTful e-commerce backend built with Java, Spring Boot, and Docker. Features JWT authentication with role-based access control, full CRUD operations, and a containerized setup ready to run with a single command.

![CI](https://github.com/Juammpa/e-commerce/actions/workflows/maven.yml/badge.svg)

## 🚀 Tech Stack

| Technology | Version |
|---|---|
| ☕ Java | 17 |
| 🍃 Spring Boot | 4.0.5 |
| 🔐 Spring Security + JWT | JWT 0.11.5 |
| 🗄️ Spring Data JPA + Hibernate | latest |
| 🐬 MySQL | 8.0 |
| 🐳 Docker + Docker Compose | latest |
| 🔧 Maven | 3.x |

## ✅ Prerequisites

- 🐳 Docker Desktop installed and running
- ☕ Java 17+
- 🔧 Maven 3.x

## ⚡ Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Juammpa/e-commerce.git
cd e-commerce
```

### 2. Configure environment variables

```bash
cp .env.example .env
```

Edit the `.env` file and fill in the required values:

```
DB_URL=jdbc:mysql://db-mysql:3306/ecommerce_db?createDatabaseIfNotExist=true
DB_USER_NAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000
```

### 3. Build the JAR

```bash
mvn clean install
```

### 4. Start all services with Docker

```bash
docker-compose up --build
```

This will start:
- 🐬 MySQL database
- 🌐 E-commerce API on port **8080**

## 🔐 Authentication

This API uses JWT Bearer token authentication.

### Register a new user

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

Use the returned token in subsequent requests:
```
Authorization: Bearer <token>
```

## 📡 API Endpoints

All endpoints are accessible at `http://localhost:8080`

### 🔓 Public

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |

### 📦 Products

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| 🟢 GET | `/api/products` | Get all products | ❌ |
| 🟢 GET | `/api/products/{id}` | Get product by ID | ❌ |
| 🔵 POST | `/api/products` | Create a product | ✅ ADMIN |
| 🟡 PUT | `/api/products/{id}` | Update a product | ✅ ADMIN |
| 🔴 DELETE | `/api/products/{id}` | Delete a product | ✅ ADMIN |

### 🗂️ Categories

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| 🟢 GET | `/api/categories` | Get all categories | ❌ |
| 🟢 GET | `/api/categories/{id}` | Get category by ID | ❌ |
| 🔵 POST | `/api/categories` | Create a category | ✅ ADMIN |
| 🟡 PUT | `/api/categories/{id}` | Update a category | ✅ ADMIN |
| 🔴 DELETE | `/api/categories/{id}` | Delete a category | ✅ ADMIN |

### 🛒 Cart

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| 🟢 GET | `/api/cart` | Get current user's cart | ✅ USER |
| 🔵 POST | `/api/cart/items` | Add item to cart | ✅ USER |
| 🟡 PUT | `/api/cart/items/{id}` | Update item quantity | ✅ USER |
| 🔴 DELETE | `/api/cart/items/{id}` | Remove item from cart | ✅ USER |

### 📋 Orders

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| 🟢 GET | `/api/orders` | Get current user's orders | ✅ USER |
| 🟢 GET | `/api/orders/{id}` | Get order by ID | ✅ USER |
| 🔵 POST | `/api/orders` | Create order from cart | ✅ USER |
| 🟡 PUT | `/api/orders/{id}/status` | Update order status | ✅ ADMIN |

## 📁 Project Structure

```
e-commerce/
├── src/
│   ├── main/
│   │   ├── java/com/micompany/ecommerce/
│   │   │   ├── controllers/       # REST controllers
│   │   │   ├── services/          # Business logic
│   │   │   ├── models/
│   │   │   │   ├── entities/      # JPA entities
│   │   │   │   └── repositories/  # Spring Data repositories
│   │   │   ├── dto/               # Data Transfer Objects
│   │   │   ├── security/          # JWT & Spring Security config
│   │   │   └── exceptions/        # Exception handling
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/                  # Unit tests (50 tests)
├── Dockerfile
├── docker-compose.yml
├── .env.example
└── pom.xml
```

## 🧪 Running Tests

```bash
mvn test
```

50 unit tests covering services for Auth, Products, Categories, Cart and Orders.

## 📝 Request Examples

**Add item to cart:**
```json
POST /api/cart/items
{
  "productId": 1,
  "quantity": 2
}
```

**Create order from cart:**
```json
POST /api/orders
{}
```

**Update order status (ADMIN):**
```json
PUT /api/orders/1/status
{
  "status": "CONFIRMED"
}
```
