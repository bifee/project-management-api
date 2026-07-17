# AGENTS.md

# Project Management API - Agent Instructions

## Project Overview

This project is a REST API for project and task management inspired by Trello and Jira.

The main objective is to keep the codebase clean, maintainable, and easy to evolve while following Domain-Driven Design (DDD), Clean Architecture, and SOLID principles.

Before implementing any feature, always understand the existing architecture and keep consistency with the current codebase.

---

# Architecture

This project follows Layered DDD.

```
api/
application/
domain/
infrastructure/
shared/
```

Responsibilities:

- **api**
    - REST Controllers
    - Request DTOs
    - Response DTOs
    - HTTP validation
    - Never contain business rules.

- **application**
    - Use Cases
    - Application Services
    - Transaction orchestration
    - Coordinates domain objects
    - No persistence implementation details.

- **domain**
    - Entities
    - Value Objects
    - Domain Services
    - Business Rules
    - Domain Exceptions

  This layer must remain independent from Spring Boot and infrastructure.

- **infrastructure**
    - JPA Entities
    - Spring Data Repositories
    - Database mappings
    - External integrations

- **shared**
    - Shared utilities
    - Error responses
    - Common exceptions
    - Constants
    - Helpers

---

# Development Guidelines

Always preserve the existing architecture.

Business rules belong only inside the Domain or Application layers.

Controllers must remain thin.

Repositories must never contain business logic.

Avoid duplicating code.

Prefer composition over inheritance.

Always follow SOLID principles.

Follow Clean Code practices.

Do not introduce unnecessary abstractions.

Keep methods small and cohesive.

---

# Java Guidelines

Target Java 17.

Prefer constructor injection.

Avoid field injection.

Never use public mutable fields.

Prefer immutable objects whenever possible.

Use Optional only as return types.

Avoid returning null.

Prefer meaningful names instead of abbreviations.

---

# Spring Guidelines

Use:

- @Service
- @RestController
- @Repository
- @Configuration

only where appropriate.

Avoid placing business logic inside Controllers.

Avoid putting business rules inside Repository implementations.

Use Validation annotations whenever possible.

---

# API Guidelines

All endpoints should:

- Return appropriate HTTP status codes
- Validate input
- Return meaningful error messages
- Follow REST conventions

Examples:

GET /projects

GET /projects/{id}

POST /projects

PUT /projects/{id}

DELETE /projects/{id}

Nested resources:

GET /projects/{id}/tasks

POST /projects/{id}/tasks

GET /tasks/{id}/comments

POST /tasks/{id}/comments

---

# Domain Rules

The domain model is the most important part of this project.

Never bypass business rules.

Whenever a business rule is added:

- Prefer putting it inside the domain model.
- Use Application Services only for orchestration.
- Infrastructure should never enforce business rules.

---

# Security

Authentication uses JWT.

Never expose sensitive information.

Always validate authenticated user permissions before allowing modifications.

Authorization examples:

- Only project members can manage tasks.
- Only project owners can manage project members.
- Only authenticated users may create resources.

---

# Persistence

Database:

- PostgreSQL

Tests may use:

- H2

Prefer Spring Data JPA.

Avoid unnecessary queries.

Avoid N+1 problems.

Use lazy loading appropriately.

---

# Testing

When implementing new functionality:

- Create unit tests whenever practical.
- Prefer testing business rules over framework behavior.
- Keep tests deterministic.

Recommended:

- JUnit 5
- Mockito
- MockMvc
- Testcontainers

---

# Documentation

Whenever new endpoints are added:

- Keep naming consistent.
- Update the README if necessary.
- Keep future OpenAPI documentation in mind.

---

# Coding Style

Priorities:

1. Readability
2. Simplicity
3. Maintainability
4. Testability
5. Performance

Never optimize prematurely.

---

# Before Finishing Any Task

Always verify:

- Does the code compile?
- Does it follow the project architecture?
- Are responsibilities in the correct layer?
- Is business logic isolated?
- Is there duplicated code?
- Can the solution be simplified?

If multiple solutions exist, choose the simplest one that preserves the architecture.

---

# What to Avoid

Do NOT:

- Mix Domain with Infrastructure
- Put business logic inside Controllers
- Put business logic inside Repositories
- Create God Classes
- Use static mutable state
- Break existing APIs without necessity
- Introduce unnecessary dependencies
- Ignore existing project conventions

---

# Goal

Every contribution should make the project easier to understand, easier to maintain, and easier to extend while preserving DDD and Clean Architecture principles.