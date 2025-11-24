Firebase Authentication Spring Boot Application
A secure Spring Boot application with Firebase Authentication, role-based authorization, and RESTful APIs.

🚀 Features
Firebase Authentication - Email/password authentication using Firebase

Role-Based Access Control - ADMIN and USER roles with proper authorization

RESTful APIs - Clean API endpoints for authentication operations

JPA & PostgreSQL - Data persistence with relational database

Docker Support - Containerized deployment

Swagger Documentation - API documentation available at /swagger-ui.html

🛠️ Tech Stack
Backend: Spring Boot 3.x, Spring Security, Spring Data JPA

Authentication: Firebase Auth

Database: PostgreSQL

Containerization: Docker

API Documentation: SpringDoc OpenAPI 3

Build Tool: Maven

📋 Prerequisites
Java 17 or higher

Maven 3.6+

PostgreSQL 12+

Firebase Project

Docker (optional)

🔧 Setup Instructions
1. Firebase Configuration
Create Firebase Project
Go to Firebase Console

Create a new project or use existing one

Enable Authentication → Email/Password provider

Get your Web API Key from Project Settings → General

Download Service Account Key
Go to Project Settings → Service Accounts

Generate new private key

Download JSON file and save as firebase.json in src/main/resources/

2. Database Setup
sql
-- Create database
CREATE DATABASE authprac;

-- The application will automatically create tables via Hibernate
3. Environment Configuration
Update application.properties with your configurations:

properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/authprac
spring.datasource.username=postgres
spring.datasource.password=your_password

# Firebase
firebase.web-api-key=your_firebase_web_api_key
4. Build and Run
Using Maven
bash
# Build the application
mvn clean package

# Run the application
java -jar target/auth-0.0.1-SNAPSHOT.jar
Using Docker
bash
# Build Docker image
docker build -t firebase-auth-app .

# Run container
docker run -p 8089:8089 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/authprac \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e FIREBASE_WEB_API_KEY=your_firebase_web_api_key \
  firebase-auth-app
📚 API Documentation
Authentication Endpoints
1. Register User
URL: POST /api/v1/auth-service/register

Description: Register a new user with Firebase and local database

Headers: Content-Type: application/json

Body:

json
{
  "name": "John Doe",
  "email": "john@example.com",
  "contactNumber": "9841234567",
  "role": "USER",
  "password": "securepassword"
}
Response:

json
{
  "message": "User registered successfully with Firebase!",
  "statusCode": 200
}
2. Firebase Login
URL: POST /api/v1/firebase/login

Description: Authenticate user with Firebase email/password

Headers: Content-Type: application/json

Body:

json
{
  "email": "john@example.com",
  "password": "securepassword"
}
Response:

json
{
  "message": "Login successful",
  "statusCode": 200,
  "refreshToken": "firebase_refresh_token",
  "role": "USER",
  "userId": 1,
  "firebaseUid": "firebase_uid",
  "firebaseToken": "firebase_id_token"
}
3. Refresh Firebase Token
URL: POST /api/v1/firebase/refresh-token

Description: Exchange refresh token for new ID token

Headers: Content-Type: application/json

Body:

json
{
  "refreshToken": "firebase_refresh_token"
}
Response:

json
{
  "statusCode": 200,
  "firebaseToken": "new_firebase_id_token",
  "refreshToken": "new_refresh_token"
}
4. Verify Firebase Token
URL: POST /api/v1/firebase/verify-token

Description: Verify Firebase ID token and get user details

Headers: Content-Type: application/json

Body:

json
{
  "idToken": "firebase_id_token"
}
Response:

json
{
  "statusCode": 200,
  "message": "Token verified successfully",
  "role": "USER",
  "userId": 1,
  "firebaseUid": "firebase_uid"
}
Protected Endpoints
All endpoints except registration and login require Firebase ID token in the header:

http
Authorization: Bearer YOUR_FIREBASE_ID_TOKEN
Role-Based Access Examples:
User Endpoints (Requires USER or ADMIN role):

java
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@GetMapping("/api/v1/user/profile")
public ResponseEntity<?> getUserProfile() {
    // User profile logic
}
Admin Endpoints (Requires ADMIN role only):

java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/v1/admin/users")
public ResponseEntity<?> getAllUsers() {
    // Admin-only logic
}
🔐 Security Configuration
The application uses Firebase Authentication with Spring Security:

Security Flow:
Request comes with Authorization: Bearer <firebase_token>

FirebaseAuthenticationFilter verifies the token with Firebase

User details are loaded from local database

Roles/Authorities are set in Spring Security context

Access control is enforced based on roles

Role Hierarchy:
