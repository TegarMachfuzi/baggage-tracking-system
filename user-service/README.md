# User Service

Authentication & User Management Service dengan BCrypt password hashing dan JWT tokens.

## Features

- ✅ User registration with validation
- ✅ User login with JWT token
- ✅ Password hashing (BCrypt)
- ✅ Role-based access (USER, STAFF, ADMIN)
- ✅ Token validation endpoint
- ✅ User management (CRUD)

## Endpoints

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register new user |
| POST | `/api/users/login` | Login and get JWT token |
| POST | `/api/users/validate` | Validate JWT token |

### Protected Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/username/{username}` | Get user by username |
| GET | `/api/users` | Get all users |
| DELETE | `/api/users/{id}` | Delete user |

## Configuration

```properties
server.port=8087

spring.datasource.url=jdbc:postgresql://localhost:5432/user_db
spring.datasource.username=postgres
spring.datasource.password=postgres

jwt.secret=mySecretKeyForBaggageTrackingSystemThatIsLongEnough
jwt.expiration=86400000
```

## Run Locally

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run
```

## Test

### 1. Register User

```bash
curl -X POST http://localhost:8087/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phone": "+1234567890"
  }'

# Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john.doe",
  "email": "john@example.com",
  "role": "USER"
}
```

### 2. Login

```bash
curl -X POST http://localhost:8087/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "password123"
  }'
```

### 3. Validate Token

```bash
TOKEN="your-jwt-token"

curl -X POST http://localhost:8087/api/users/validate \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Get User Info

```bash
curl http://localhost:8087/api/users/username/john.doe
```

## Roles

- **USER** - Regular passenger (default)
- **STAFF** - Airport staff
- **ADMIN** - System administrator

## Security

- Passwords hashed with BCrypt (strength 10)
- JWT tokens with 24-hour expiration
- Role-based access control
- Input validation

## Database Schema

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

## Integration with API Gateway

Update API Gateway to call user-service for authentication:

```java
// In AuthController
@Autowired
private RestTemplate restTemplate;

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // Call user-service
    String url = "http://localhost:8087/api/users/login";
    return restTemplate.postForEntity(url, request, AuthResponse.class);
}
```

## Status

**COMPLETE** - Production Ready ✅

- ✅ BCrypt password hashing
- ✅ JWT token generation
- ✅ Role management
- ✅ Input validation
- ✅ Error handling

---

**Last Updated**: 2026-03-03
