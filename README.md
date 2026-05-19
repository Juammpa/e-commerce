# 🛒 E-Commerce REST API

A full-featured RESTful e-commerce API built with Java and Spring Boot, featuring JWT authentication, role-based access control, and Docker support.

## 🚀 Tech Stack

| Technology | Version |
|------------|---------|
| ☕ Java | 17 |
| 🍃 Spring Boot | 4.0.5 |
| 🔐 Spring Security + JWT | 0.11.5 |
| 🗄️ Spring Data JPA + Hibernate | latest |
| 🐬 MySQL | 8.0 |
| 🐳 Docker | latest |
| 🔧 Maven | 3.x |
| 📦 Lombok | latest |

## 👥 Roles

| Role | Permissions |
|------|-------------|
| `ADMIN` | Full access — manages products, categories, users and all orders |
| `CUSTOMER` | Can register, browse products, manage cart and own orders |

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

### 2. Build the JAR file

```bash
mvn clean install -DskipTests
```

### 3. Start with Docker

```bash
docker build -t e-commerce .
docker run -p 8080:8080 e-commerce
```

## 🔐 Authentication

### Register

```
POST /api/auth/register
```
```json
{
    "email": "user@email.com",
    "password": "123456",
    "rol": "CUSTOMER"
}
```

### Login

```
POST /api/auth/login
```
```json
{
    "email": "user@email.com",
    "password": "123456"
}
```

Returns a JWT token. Include it in all protected requests: 
```Authorization: Bearer {token}```

## 📡 API Endpoints

### 🔓 Public

| Method | Endpoint | Description |
|--------|----------|-------------|
| 🔵 POST | `/api/auth/register` | Register a new user |
| 🔵 POST | `/api/auth/login` | Login and get JWT token |
| 🟢 GET | `/api/products` | Get all products |
| 🟢 GET | `/api/products/{id}` | Get product by id |
| 🟢 GET | `/api/categories` | Get all categories |

### 🔒 CUSTOMER

| Method | Endpoint | Description |
|--------|----------|-------------|
| 🟢 GET | `/api/cart` | Get own cart |
| 🔵 POST | `/api/cart/items` | Add item to cart |
| 🟡 PUT | `/api/cart/items/{itemId}` | Update cart item quantity |
| 🔴 DELETE | `/api/cart/items/{itemId}` | Remove item from cart |
| 🔴 DELETE | `/api/cart` | Clear cart |
| 🔵 POST | `/api/orders` | Create order from cart |
| 🟢 GET | `/api/orders/my-orders` | Get own orders |
| 🟢 GET | `/api/orders/{id}` | Get order by id |

### 🔑 ADMIN

| Method | Endpoint | Description |
|--------|----------|-------------|
| 🔵 POST | `/api/categories` | Create category |
| 🟡 PUT | `/api/categories/{id}` | Update category |
| 🔴 DELETE | `/api/categories/{id}` | Delete category |
| 🔵 POST | `/api/products` | Create product |
| 🟡 PUT | `/api/products/{id}` | Update product |
| 🔴 DELETE | `/api/products/{id}` | Delete product |
| 🟢 GET | `/api/orders` | Get all orders |
| 🟡 PUT | `/api/orders/{id}/status` | Update order status |
| 🟢 GET | `/api/orders/{id}` | Get order by id |

## 📋 Order Status Flow
- ```PENDING → CONFIRMED → SHIPPED → DELIVERED```
- ```PENDING → CANCELLED```

## 📁 Project Structure
```bash
src/
├── controller/
│   ├── AuthController
│   ├── CartController
│   ├── CategoryController
│   ├── OrderController
│   └── ProductController
├── dto/
│   ├── auth/
│   ├── carts/
│   ├── categories/
│   ├── orders/
│   └── products/
├── mapper/
├── model/
│   ├── entities/
│   └── enums/
├── repository/
├── security/
└── service/
```
