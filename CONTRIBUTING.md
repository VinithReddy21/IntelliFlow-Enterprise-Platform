# Contributing to IntelliFlow AI Platform

Thank you for contributing to IntelliFlow AI! As an enterprise-grade platform, we maintain strict code quality standards, automated linting, and systematic branch policies.

## Code Standards & Conventions

### 1. Java Backend (Spring Boot)
- **Code Style**: Google Java Style Guide.
- **Architecture**: Modular Monolith organized by domain features (`com.intelliflow.modules.*`).
- **Dependencies**: Use Lombok for boilerplate reduction (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`).
- **Immutability**: Prefer `record` types or final fields for DTOs.
- **Database Access**: All SQL schema changes MUST be managed via Flyway migration scripts in `src/main/resources/db/migration`. Never rely on JPA schema generation.

### 2. Python AI Microservice (FastAPI)
- **Code Style**: PEP 8 formatted using `black` and `isort`.
- **Validation**: All API inputs/outputs MUST be typed using Pydantic v2 schemas.
- **Async Execution**: Use `async def` for network and I/O bound endpoints.

---

## Git Workflow & Branching Strategy

1. **Branch Naming**:
   - Features: `feature/TASK-123-short-description`
   - Bugfixes: `fix/TASK-456-short-description`
   - Documentation: `docs/short-description`
2. **Commit Messages**:
   Follow Conventional Commits:
   - `feat(auth): implement refresh token rotation`
   - `fix(task): resolve duplicate task creation bug`
   - `docs(adr): add ADR-006 for modular monolith`

---

## Pull Request Checklist
Before requesting review:
- [ ] Code compiles cleanly without compiler warnings.
- [ ] Flyway migrations executed and validated.
- [ ] Unit and Integration tests written and passing (`mvn test` / `pytest`).
- [ ] OpenAPI documentation annotations updated.
