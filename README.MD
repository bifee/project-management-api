# Project Management API (Java + Spring Boot + DDD)

A REST API for managing **projects** and **tasks**, inspired by tools like **Trello/Jira**, built with **Java** and **Spring Boot**, following **DDD (Domain-Driven Design)**, **Clean Architecture** and **Design Patterns** concepts.


---

### Overview
**Project Management API** is a REST API for teams that need to organize **projects**, **tasks**, and **comments**, with a foundation designed to grow as business rules become more complex.

The project is structured to keep the domain model isolated from infrastructure details, improving maintainability, testability, and clarity of business rules.

### Main features (current / planned)
- User authentication (**JWT**)
- **Projects** CRUD
- **Tasks** CRUD linked to projects
- Task **comments**
- Context-based authorization rules (e.g., only project members can manage tasks; only the owner can manage members)

> Note: depending on the current state of the repository, some endpoints may still be under development.

### Core entities
- **User**
- **Project**
- **Task**
- **Comment**

### Architecture (Layered DDD)
The codebase follows a layered architecture with separated responsibilities:

- **api/**: REST controllers (HTTP entrypoint) and request/response DTOs
- **application/**: services/use cases (application rules and orchestration)
- **domain/**: domain model (entities, value objects, business rules)
- **infrastructure/**: persistence and integrations (JPA entities, repositories, mappings)
- **shared/**: reusable cross-cutting utilities (e.g., error responses, shared exceptions)

### Tech stack
- **Java 17**
- **Spring Boot** (Web, Security, Validation, Data JPA)
- **PostgreSQL** (production)
- **H2** (for tests/local usage when applicable)
- **JWT** (Auth0 java-jwt)
- **Maven**

### How to run (high level)
1. Configure environment/`application.properties` (DB, JWT secret, etc.)
2. Start PostgreSQL (locally or using Docker)
3. Run:
   ```bash
   mvn spring-boot:run
   ```

### Recommended next steps
- Add **unit tests** (especially for `application` services)
- Add **integration tests** for endpoints (MockMvc / Testcontainers)
- Add API documentation with **OpenAPI/Swagger**
- Standardize error handling with a **Global Exception Handler** (`@RestControllerAdvice`)
- Add pagination/filtering for listing endpoints