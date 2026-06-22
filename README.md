# 🔐 Spring Security - JWT Authentication REST API

A production-ready **Spring Boot 4.1.0** REST API demonstrating authentication and authorization using **Spring Security 7.1.0**, **BCrypt password encoding**, **JPA (MySQL)**, and **JWT (JSON Web Token)** setup.

> Built as a learning project to understand Spring Security internals — from filter chains to custom authentication providers.

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Architecture Overview](#-architecture-overview)
- [Project Structure](#-project-structure)
- [API Endpoints](#-api-endpoints)
- [Authentication Flow](#-authentication-flow)
- [Database Schema](#-database-schema)
- [Setup & Run](#-setup--run)
- [Key Concepts for Interviews](#-key-concepts-for-interviews)
- [Changelog](#-changelog)

---

## 🛠 Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| **Spring Boot** | 4.1.0 | Application framework |
| **Spring Security** | 7.1.0 | Authentication & Authorization |
| **Spring Data JPA** | 4.1.0 | ORM / Database access |
| **MySQL** | 8.0+ | Relational database |
| **JJWT** | 0.12.6 | JWT token generation & validation |
| **Java** | 17 | Language |
| **Maven** | - | Build tool |

---

## 🏗 Architecture Overview

```
Client (Postman / Frontend)
        │
        ▼
┌──────────────────────────────────────────────────┐
│              Spring Security Filter Chain         │
│                                                   │
│  DisableEncodeUrlFilter                           │
│  → SecurityContextHolderFilter                    │
│  → HeaderWriterFilter                             │
│  → LogoutFilter                                   │
│  → BasicAuthenticationFilter                      │
│  → AnonymousAuthenticationFilter                  │
│  → ExceptionTranslationFilter                     │
│  → AuthorizationFilter                            │
└──────────────────────┬───────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────┐
│                REST Controllers                   │
│                                                   │
│  UserController ──→ UserService                   │
│       │                   │                       │
│       │          AuthenticationManager             │
│       │                   │                       │
│       │          DaoAuthenticationProvider         │
│       │                   │                       │
│       │          MyUserDetailsService              │
│       │                   │                       │
│       ▼                   ▼                       │
│  UserRepository ◄──── JPA / MySQL                 │
└──────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
src/main/java/com/jai/SpringSecurity/
│
├── SpringSecurityApplication.java          # Main entry point
│
├── config/
│   └── SecurityConfig.java                 # Security filter chain, AuthProvider, AuthManager
│
├── Controller/
│   ├── UserController.java                 # /register, /login endpoints
│   └── StudentController.java             # /student CRUD (protected)
│
├── Entity/
│   ├── Users.java                          # JPA entity mapped to MySQL
│   ├── UserPrinciple.java                  # UserDetails implementation (Spring Security adapter)
│   └── Student.java                        # In-memory student model
│
├── Service/
│   ├── UserService.java                    # Business logic: register & verify (authenticate)
│   └── MyUserDetailsService.java           # Custom UserDetailsService - loads user from DB
│
└── Repository/
    └── UserRepository.java                 # JPA repository with findByUsername()
```

---

## 🔗 API Endpoints

### Public Endpoints (No Authentication Required)

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `POST` | `/register` | Register a new user | `{"username": "sam", "password": "s@123"}` |
| `POST` | `/login` | Authenticate & get response | `{"username": "sam", "password": "s@123"}` |

### Protected Endpoints (Authentication Required)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/student` | Get all students | HTTP Basic |
| `POST` | `/student` | Add a student | HTTP Basic |
| `GET` | `/csrf-token` | Get CSRF token (disabled) | HTTP Basic |

---

## 🔄 Authentication Flow

### Registration (`POST /register`)
```
1. Client sends { username, password }
2. UserController receives the request
3. Password is encoded using BCryptPasswordEncoder (strength: 12)
4. Encoded user is saved to MySQL via JPA
5. Response: saved user object (with hashed password)
```

### Login (`POST /login`)
```
1. Client sends { username, password }
2. UserController calls UserService.verify()
3. AuthenticationManager.authenticate() is invoked
4. DaoAuthenticationProvider delegates to MyUserDetailsService
5. MyUserDetailsService loads user from DB via UserRepository.findByUsername()
6. User is wrapped in UserPrinciple (implements UserDetails)
7. BCryptPasswordEncoder.matches(rawPassword, encodedPassword) is called
8. If match → Authentication SUCCESS → returns "success"
9. If no match → BadCredentialsException → returns 401
```

### How Password Verification Works
```
Registration:  "s@123"  →  BCrypt encode  →  "$2a$12$UPymd1iqa65..."  →  stored in DB
Login:         "s@123"  →  BCrypt matches("s@123", "$2a$12$UPymd1iqa65...")  →  true ✓
```

---

## 🗄 Database Schema

**Database:** `SpringSecurityUserDatas` (MySQL)

**Table:** `users`

| Column | Type | Description |
|---|---|---|
| `id` | `INT` (PK, Auto) | Primary key |
| `username` | `VARCHAR(255)` | Unique username |
| `password` | `VARCHAR(255)` | BCrypt hashed password |

> ⚠️ The `id` field uses `Integer` (not primitive `int`) to allow `null` during JSON deserialization when the client doesn't send an ID (e.g., login/register payloads).

---

## 🚀 Setup & Run

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven

### 1. Create MySQL Database
```sql
CREATE DATABASE SpringSecurityUserDatas;
```

### 2. Configure Database Connection
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/SpringSecurityUserDatas
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 3. Run the Application
```bash
mvn spring-boot:run
```
The app starts on `http://localhost:8080`

### 4. Test with Postman

**Register:**
```bash
POST http://localhost:8080/register
Content-Type: application/json

{
    "username": "sam",
    "password": "s@123"
}
```

**Login:**
```bash
POST http://localhost:8080/login
Content-Type: application/json

{
    "username": "sam",
    "password": "s@123"
}
# Response: "success"
```

**Access Protected Resource:**
```bash
GET http://localhost:8080/student
Authorization: Basic c2FtOnNAMTIz    # sam:s@123 in Base64
```

---

## 🎯 Key Concepts for Interviews

### 1. Why `BCryptPasswordEncoder(12)`?
- BCrypt is a one-way hashing algorithm — you **cannot** decrypt the stored password
- The strength parameter `12` means 2^12 = 4096 hashing rounds, balancing security and performance
- Same encoder must be used for both **registration** (encoding) and **authentication** (matching)

### 2. Why Stateless Sessions?
```java
http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
```
- No server-side session storage — every request must carry its own credentials
- Essential for REST APIs and JWT-based authentication
- Scales horizontally without sticky sessions

### 3. Why Custom `UserDetailsService`?
- Spring Security doesn't know how your users are stored (MySQL, MongoDB, LDAP, etc.)
- `MyUserDetailsService` bridges Spring Security and your database
- `loadUserByUsername()` is called automatically by `DaoAuthenticationProvider`

### 4. Why `UserPrinciple` (implements `UserDetails`)?
- Spring Security works with `UserDetails`, not your custom `Users` entity
- `UserPrinciple` is an **adapter** that wraps `Users` and exposes the required methods:
  - `getUsername()`, `getPassword()`, `getAuthorities()`

### 5. Why `Integer id` instead of `int id`?
- JSON payloads like `{"username": "sam", "password": "s@123"}` don't include `id`
- Primitive `int` cannot be `null` → Jackson throws `HttpMessageNotReadableException`
- `Integer` wrapper allows `null`, enabling proper deserialization

### 6. Spring Security Filter Chain (Order Matters!)
```
Request → DisableEncodeUrlFilter → SecurityContextHolderFilter → HeaderWriterFilter
→ LogoutFilter → BasicAuthenticationFilter → AnonymousAuthenticationFilter
→ ExceptionTranslationFilter → AuthorizationFilter → Controller
```

### 7. `permitAll()` vs `authenticated()`
```java
.requestMatchers("/login", "/register").permitAll()    // No auth needed
.anyRequest().authenticated()                          // Everything else needs auth
```

---

## 📝 Changelog

### v1.1.0 — Login & Registration with DB Authentication (2026-06-22)
- ✅ Added `POST /login` endpoint with `AuthenticationManager` verification
- ✅ Added `POST /register` endpoint with BCrypt password hashing
- ✅ Created `Users` JPA entity mapped to MySQL
- ✅ Created `UserPrinciple` implementing `UserDetails` (adapter pattern)
- ✅ Created `MyUserDetailsService` for DB-backed user loading
- ✅ Created `UserRepository` with `findByUsername()` query method
- ✅ Configured `DaoAuthenticationProvider` with `BCryptPasswordEncoder(12)`
- ✅ Configured `AuthenticationManager` bean via `AuthenticationConfiguration`
- ✅ Added JJWT dependencies (0.12.6) for future JWT token support
- 🐛 Fixed: Request matcher patterns now include leading `/` (Spring Security 7.x requirement)
- 🐛 Fixed: `Users.id` changed from `int` → `Integer` to prevent JSON deserialization errors
- 🐛 Fixed: Removed hardcoded `spring.security.user.*` properties that conflicted with DB auth

### v1.0.0 — Initial Security Setup
- ✅ Spring Boot 4.1.0 project with Spring Security 7.1.0
- ✅ CSRF disabled for REST API usage
- ✅ Stateless session management
- ✅ HTTP Basic authentication
- ✅ In-memory student CRUD endpoints (`/student`)

---

## 🔮 Upcoming Features

- [ ] JWT token generation on `/login` (using JJWT library)
- [ ] JWT filter for token-based request authentication
- [ ] Role-based access control (`ADMIN`, `USER`)
- [ ] Refresh token mechanism
- [ ] Global exception handling with proper error responses

---

## 📄 License

This project is for learning purposes.

---

*Built with ☕ and Spring Security by [Jai](https://github.com/jayakrishna9655)*
