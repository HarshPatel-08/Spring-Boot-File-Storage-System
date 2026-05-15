# 📁 Practice — Spring Boot File Management API

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.14-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
  <img src="https://img.shields.io/badge/MinIO-Object_Storage-C72E49?style=for-the-badge&logo=minio&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>
</p>

A production-ready **RESTful File Management API** built with Spring Boot. Features JWT-based authentication, role-based access control, and MinIO object storage integration for secure file upload, download, streaming, and pre-signed URL generation.

---

## ✨ Features

- 🔐 **JWT Authentication** — Stateless auth with token-based login and registration
- 🛡️ **Role-Based Access Control** — `USER` and `ADMIN` roles enforced via Spring Security
- ☁️ **MinIO Object Storage** — S3-compatible storage for all file operations
- 📤 **File Upload** — Upload files up to 10 MB directly through the API
- 📥 **File Download** — Stream or download files by ID
- 🔗 **Pre-signed URLs** — Generate temporary public access URLs for files
- 🔍 **File Search & Pagination** — Search personal files with paginated results
- 📄 **Swagger UI** — Fully documented interactive API explorer
- ✅ **Bean Validation** — Request payload validation with `jakarta.validation`
- 🚨 **Global Exception Handling** — Consistent error responses across all endpoints

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.14 |
| Security | Spring Security + JJWT 0.12.x |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| Object Storage | MinIO 8.5.7 |
| API Docs | SpringDoc OpenAPI (Swagger UI) 2.6.0 |
| Build Tool | Maven |
| Validation | Spring Boot Starter Validation |

---

## 📋 Prerequisites

Before running the project, ensure you have the following installed:

- **Java 17+**
- **Maven 3.8+**
- **MySQL 8.0+**
- **MinIO Server** (running locally or remotely)

---

## ⚙️ Configuration

Copy the example properties file and fill in your values:

```bash
cp src/main/resources/applicaion_example.properties src/main/resources/application.properties
```

Edit `application.properties`:

```properties
# Application
spring.application.name=Practice

# MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3306/file_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# File Upload Limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# MinIO
minio.url=http://127.0.0.1:9000
minio.access.name=YOUR_MINIO_ACCESS_KEY
minio.access.secret=YOUR_MINIO_SECRET_KEY
minio.bucket.name=my-files

# JWT
jwt.secret=YOUR_SECRET_KEY_MIN_32_CHARS
jwt.expiration=3600000
```

> ⚠️ **Never commit `application.properties` with real credentials to version control.**

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/Practice.git
cd Practice
```

### 2. Create the MySQL database

```sql
CREATE DATABASE file_db;
```

### 3. Start MinIO (Docker)

```bash
docker run -p 9000:9000 -p 9001:9001 \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  quay.io/minio/minio server /data --console-address ":9001"
```

### 4. Build and run

```bash
mvn clean install
mvn spring-boot:run
```

The API will be available at: **`http://localhost:8080`**

---

## 📚 API Reference

### 🔑 Auth Endpoints

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `POST` | `/auth/register` | ❌ | Register a new user |
| `POST` | `/auth/login` | ❌ | Login and receive JWT token |

#### Register — `POST /auth/register`

```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "confirmPassword": "SecurePass123"
}
```

#### Login — `POST /auth/login`

```json
{
  "username": "john_doe",
  "password": "SecurePass123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 📁 File Endpoints

> All file endpoints require the `Authorization: Bearer <token>` header.

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `POST` | `/file/upload` | ✅ USER | Upload a file (multipart/form-data) |
| `GET` | `/file/download?id={id}` | ✅ USER | Download a file by ID |
| `GET` | `/file/view?id={id}` | ✅ USER | Stream/view a file inline |
| `GET` | `/file/url/{id}` | ✅ USER | Get a pre-signed URL for a file |
| `GET` | `/file/my-files` | ✅ USER | List your files (paginated) |
| `GET` | `/file/my-files/{search}` | ✅ USER | Search your files by name |
| `GET` | `/file/admin/test` | ✅ ADMIN | Admin-only endpoint |

#### Upload a File

```bash
curl -X POST http://localhost:8080/file/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/yourfile.pdf"
```

#### List My Files (Paginated)

```
GET /file/my-files?page=0&size=5
```

#### Search Files

```
GET /file/my-files/report?page=0&size=5
```

---

## 🌐 Swagger UI

Interactive API documentation is available after starting the application:

```
http://localhost:8080/swagger-ui.html
```

Use the **Authorize** button in Swagger UI to enter your JWT token and test all endpoints directly.

---

## 🗂️ Project Structure

```
src/
└── main/
    └── java/com/springProject/Practice/
        ├── config/
        │   ├── MinioConfig.java          # MinIO client bean
        │   ├── PasswordConfig.java       # BCrypt password encoder
        │   └── SwaggerConfig.java        # OpenAPI configuration
        ├── controller/
        │   ├── AuthController.java       # /auth endpoints
        │   └── FileController.java       # /file endpoints
        ├── dto/
        │   ├── LoginRequest.java
        │   ├── LoginResponse.java
        │   ├── RegisterRequest.java
        │   ├── RegisterResponse.java
        │   ├── FileResponse.java
        │   ├── AuthResponse.java
        │   ├── ResponseAPI.java
        │   └── ErrorResponse.java
        ├── exception/
        │   ├── GlobalExceptionHandler.java
        │   ├── AccessDeniedException.java
        │   ├── AuthenticationException.java
        │   ├── FileNotFoundException.java
        │   ├── FileStorageException.java
        │   ├── InvalidFileException.java
        │   ├── PasswordMismatchException.java
        │   ├── UserAlreadyExistsException.java
        │   └── UserNotFoundException.java
        ├── model/
        │   ├── User.java
        │   ├── Role.java
        │   └── FileData.java
        ├── repository/
        │   ├── UserRepository.java
        │   └── FileRepository.java
        ├── security/
        │   ├── JwtFilter.java            # JWT request filter
        │   ├── JwtService.java           # Token generation & validation
        │   └── SecurityConfig.java       # Security filter chain
        ├── service/
        │   ├── AuthService.java          # Registration & login logic
        │   ├── FileService.java          # File service interface
        │   └── FileServiceImpl.java      # MinIO file operations
        └── PracticeApplication.java      # Entry point
```

---

## 🔒 Security Model

- Passwords are hashed using **BCrypt** before storage.
- All API endpoints except `/auth/**` and Swagger paths require a valid **JWT Bearer token**.
- The `ADMIN` role is enforced at the method level using `@PreAuthorize("hasRole('ADMIN')")`.
- CSRF is disabled (stateless REST API design).

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

<p align="center">Built with ❤️ using Spring Boot</p>