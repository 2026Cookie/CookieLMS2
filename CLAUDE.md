# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build the project
./gradlew build

# Run the application (default port 8080)
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.wanted.cookielms.CookieLmsApplicationTests"

# Clean build artifacts
./gradlew clean
```

## Database Setup

MySQL is required. Run the SQL scripts in `src/main/resources/sql/` in order:

1. `setDatabase.sql` — creates database `CoookieLMS` (note triple-o) and user `cookie`/`cookie`
2. `setTable.sql` — creates all 9 tables
3. `setDumpData.sql` — loads sample data

The application connects as user `cookie` with password `cookie` to localhost.

## Architecture

Spring Boot 3.x MVC application with Thymeleaf templates and Spring Security.

**Package layout** under `com.wanted.cookielms`:

- `global/` — application entry point, Spring configuration beans (`ContextConfig`, `JpaConfig`), and the root `IndexController`
- `domain/` — JPA entity classes (e.g., `Enrollment`)

**Layered conventions expected as features are added:**
- Controllers handle HTTP routing and delegate to services
- Services hold business logic
- Repositories extend Spring Data JPA interfaces
- DTOs are mapped using `ModelMapper` (bean defined in `ContextConfig`)

**Security**: Role-based access via Spring Security. Three roles exist in the DB schema: `USER`, `ADMIN`, `INSTRUCTOR`. Current routes: `GET /` (open), `GET /admin/page` (admin), `GET /user/page` (user).

**Domain model (from DB schema):**
- `users` — accounts with role and status
- `lecture` — courses with capacity and schedule
- `enrollment` / `waitlist` — student enrollment flow
- `assignments` / `assignment_submissions` — assignment lifecycle
- `user_bans`, `user_logs`, `api_performance_logs` — audit/moderation

**JPA config** (`JpaConfig`): enables repositories and scans entities under `com.wanted.cookielms`.

**Component scan** (`ContextConfig`): base package `com.wanted.cookielms`.

## Key Dependencies

- Spring Boot 3.x — web, data-jpa, security
- Thymeleaf + `thymeleaf-extras-springsecurity6`
- Lombok
- ModelMapper 3.1.1
- MySQL Connector/J
- Spring Boot DevTools (development only)
- Spring Security Test (tests)
