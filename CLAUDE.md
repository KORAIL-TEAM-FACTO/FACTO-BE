# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FACTO_BE is a Spring Boot 3.4.5 backend application built with Java 21. It's a user management system with JWT-based authentication, supporting both USER and ADMIN roles.

## Build & Development Commands

### Build & Run
```bash
./gradlew clean build        # Clean and build the project
./gradlew bootRun            # Run the application
./gradlew build --info       # Build with detailed output
```

### Testing
```bash
./gradlew test               # Run all tests
./gradlew test --tests ClassName     # Run a specific test class
./gradlew test --tests ClassName.methodName  # Run a specific test method
```

## Architecture & Code Structure

### Package Organization (Domain-Driven Design)
The codebase follows a domain-driven structure under `team.java.facto_be`:

```
src/main/java/team/java/facto_be/
├── domain/              # Business domains (currently: user)
│   └── user/
│       ├── controller/  # REST endpoints
│       ├── service/     # Business logic
│       ├── repository/  # Data access
│       ├── entity/      # JPA entities (*JpaEntity suffix)
│       ├── dto/         # Request/Response objects
│       │   ├── request/
│       │   └── response/
│       └── facade/      # Cross-cutting concerns (e.g., UserFacade for currentUser)
└── global/              # Shared infrastructure
    ├── security/        # Security configuration & JWT
    ├── filter/          # Servlet filters
    └── entity/          # Base entities (e.g., BaseTimeEntity)
```

### Key Architectural Patterns

#### 1. Entity Naming Convention
- JPA entities are suffixed with `JpaEntity` (e.g., `UserJpaEntity`)
- This separates persistence concerns from domain models
- Entities extend `BaseTimeEntity` for automatic timestamp tracking

#### 2. Security & Authentication
- **JWT-based stateless authentication** with access/refresh token pattern
- Access tokens contain: subject (email), role, type, expiration
- Refresh tokens stored in Redis for fast lookup and invalidation
- `UserFacade.currentUser()` retrieves authenticated user from SecurityContext
- Security is configured via `SecurityConfig` with custom `FilterConfig` and `JwtTokenFilter`
- Currently all endpoints are permitAll (line 55 in SecurityConfig.java) - configure authorization as needed

#### 3. Authentication Flow
- Login → `UserLoginService` validates credentials → `JwtTokenProvider.generateToken()`
- Token refresh → Validate refresh token from Redis → Issue new token pair
- Authenticated requests → `JwtTokenFilter` extracts token → `JwtTokenProvider.getAuthentication()` → SecurityContext
- Role-based UserDetails loading: `CustomUserDetailsService` (USER) or `CustomAdminDetailsService` (ADMIN)

#### 4. DTO Pattern
- Use Java records for immutable DTOs
- Response DTOs have static `from(Entity)` factory methods (e.g., `UserInfoResponse.from(user)`)
- Request DTOs use Jakarta validation annotations (@Valid)

#### 5. Service Layer Transactions
- Services are annotated with `@Transactional` (write operations) or `@Transactional(readOnly = true)` (read operations)
- Use `@Service` for business logic, not `@Component`

#### 6. Facade Pattern
- `UserFacade` provides common user operations across services
- Use facades to avoid circular dependencies and centralize cross-cutting logic
- Example: `currentUser()` encapsulates SecurityContext access and user lookup

## Technology Stack

- **Framework**: Spring Boot 3.4.5, Spring Security, Spring Data JPA
- **Database**: MySQL (with JPA batch operations enabled)
- **Caching**: Redis (for refresh token storage)
- **Authentication**: JWT (jjwt 0.11.5)
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **HTTP Client**: Spring Cloud OpenFeign
- **Validation**: Jakarta Validation
- **Data Format**: Jackson (JSON with snake_case, XML support)

## Configuration Notes

### Environment Variables
Key environment variables (with defaults in application.yml):
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` - MySQL connection
- `REDIS_HOST`, `REDIS_PORT` - Redis connection
- `JWT_SECRET`, `JWT_HEADER`, `JWT_PREFIX` - JWT configuration

### Database
- JPA DDL auto mode: `update` (consider `validate` for production)
- Batch inserts/updates enabled for performance
- `open-in-view: false` to prevent lazy loading issues

### Virtual Threads
- Java 21 virtual threads enabled (`spring.threads.virtual.enabled: true`)
- Better concurrency handling for I/O-bound operations

## Common Development Tasks

### Adding a New API Endpoint
1. Create request/response DTOs in `domain/{domain}/dto/`
2. Implement business logic in `domain/{domain}/service/`
3. Add controller method in `domain/{domain}/controller/`
4. Update SecurityConfig if authorization rules are needed
5. Follow existing patterns: services use facades, DTOs use records

### Adding a New Domain
1. Create package structure: `domain/{domain}/` with controller, service, repository, entity, dto subdirectories
2. Create JPA entity extending `BaseTimeEntity`
3. Create repository interface extending `JpaRepository`
4. Create service with `@Transactional` methods
5. Create controller with REST endpoints
6. Consider adding a Facade if cross-cutting operations are needed

### Working with Authentication
- Get current user: inject `UserFacade` and call `currentUser()`
- Generate tokens: inject `JwtTokenProvider` and call `generateToken(email, role)`
- Token validation happens automatically via `JwtTokenFilter`
- Refresh tokens: call `JwtTokenProvider.reissue(refreshToken)`

## Code Style Notes

- Use Lombok annotations (@RequiredArgsConstructor, @Getter, @SuperBuilder) to reduce boilerplate
- Constructor injection via @RequiredArgsConstructor (preferred over field injection)
- Records for DTOs (immutable by default)
- Snake_case for JSON serialization (configured globally)
- Validation via Jakarta annotations on request DTOs
