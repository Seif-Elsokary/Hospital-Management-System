# 🏥 Hospital Management System

A complete and secure RESTful Hospital Management System API built with **Java Spring Boot**, supporting operations such as managing patients, doctors, appointments, rooms, and medical records with role-based access and JWT authentication.

---

## 📌 Features

- ✅ **Secure JWT Authentication & Authorization**
- ✅ **Role-Based Access Control** (`ADMIN`, `USER`)
- ✅ **Patient & Doctor Management**
- ✅ **Appointment Scheduling**
- ✅ **Room Assignment to Patients**
- ✅ **Swagger API Documentation**
- ✅ **Search & Filter Capabilities**
- ✅ **Email Integration using SMTP**
- ✅ **Validation, DTOs, Clean Architecture**

---

## 🧱 Entity Overview

### Main Entities

| Entity          | Purpose                            |
|-----------------|-------------------------------------|
| `User`          | Authentication and role management |
| `Patient`       | Holds patient information          |
| `Doctor`        | Doctor data, specialty, experience |
| `Appointment`   | Links doctor and patient with time |
| `Room`          | Tracks hospital rooms and beds     |
| `MedicalRecord` | Patient's health history           |

### Enums Used

- `Role`: ADMIN, USER  
- `Gender`: MALE, FEMALE  
- `Specialty`: e.g. INTERNAL_MEDICINE, PEDIATRICS, etc.  
- `Disease`: e.g. CANCER, DIABETES, etc.

---

## 🔐 Security Setup

- **Authentication**: JWT (HS512 algorithm)
- **Authorization**: Spring Security with role-based filters
- **Token**: Sent via `Authorization: Bearer <token>`
- **Password Encryption**: BCrypt

---

## 🛠 Tech Stack

| Component    | Technology           |
|--------------|----------------------|
| Backend      | Java 17              |
| Framework    | Spring Boot 3.x      |
| Security     | Spring Security + JWT|
| Database     | MySQL                |
| ORM          | Spring Data JPA      |
| Docs         | Swagger / OpenAPI    |
| Mail         | JavaMail (SMTP)      |
| Testing      | JUnit, Mockito       |
| Build Tool   | Maven                |

---

## 🚀 Getting Started

### 1. ⬇️ Clone the Repository

```bash
git clone https://github.com/your-username/Hospital-Management-System.git
cd Hospital-Management-System
```

### 2. 🛢️ Set up the Database

Create a MySQL database:

```sql
CREATE DATABASE hospital_management_system;
```

### 3. ⚙️ Configure `application.properties`

Update `src/main/resources/application.properties` with your credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_management_system
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

spring.jpa.hibernate.ddl-auto=update

auth.token.jwtSecret=YOUR_BASE64_ENCODED_SECRET_KEY
auth.token.expirationTime=36000000

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL_ADDRESS
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> ✅ **Note**: `auth.token.jwtSecret` must be a **Base64-encoded key** with **at least 512 bits** for HS512.

You can generate it in Java:
```java
Keys.secretKeyFor(SignatureAlgorithm.HS512)
```

---

## 🧪 Running the Application

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Then visit: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 📒 API Access and Roles

| Endpoint                  | Access Roles         |
|---------------------------|----------------------|
| `/api/patients/**`        | `USER`, `ADMIN`      |
| `/api/doctors/**`         | `ADMIN`              |
| `/api/appointments/**`    | `USER`, `ADMIN`      |
| `/api/rooms/**`           | `USER`, `ADMIN`      |
| `/api/auth/**`            | Public (Login/Register) |
| `/swagger-ui/**`          | Public (Docs)        |

---

## 💡 Sample Usage (via Postman)

### 1. 🔐 Login to Receive Token

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "your_password"
}
```

Response:
```json
{
  "token": "your_generated_jwt_token"
}
```

### 2. 🔄 Use Token to Access Protected APIs

```http
POST /api/patients
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  ...
}
```

---

## 📦 Recommended Git Ignore

Make sure `.gitignore` includes:

```bash
*.class
/target/
*.log
.env
application.properties
```

---

## 📌 Next Improvements

- Add Nurse & Medication Modules  
- Enable Dockerfile for containerization  
- Add audit trail and logging  

---

## 👨‍💻 Author

GitHub: [[GitHub](https://github.com/Seif-Elsokary)]  
LinkedIn: [[LinkedIn](https://www.linkedin.com/in/seif-elsokary-350233256/)]  

---

## ⭐️ Support

If you find this project useful, consider starring the repository ⭐ and sharing with your peers.
