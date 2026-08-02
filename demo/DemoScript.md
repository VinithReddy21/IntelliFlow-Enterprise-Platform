# Live Interview Demonstration Script

This script provides a step-by-step walkthrough for demonstrating the **IntelliFlow AI Platform** during technical interviews or project showcases.

---

## 📍 Phase 1 Demo: Infrastructure & Baseline Environment

### Step 1: Explain the Polyglot Architecture
> *"IntelliFlow AI is built as an enterprise business operations platform. We chose a dual-service polyglot architecture: Java 21 Spring Boot 3.2 handles our main transactional business domain, while Python 3.11 FastAPI runs as an isolated AI engine microservice."*

### Step 2: Show Docker Compose Orchestration
```bash
# Demonstrate full container stack initialization
docker-compose up -d

# Verify healthy containers
docker-compose ps
```
> *"Notice how we orchestrate PostgreSQL 16 (with pgvector), Redis 7.2, Spring Boot, and FastAPI using healthchecks (`service_healthy`) to ensure proper dependency startup ordering."*

### Step 3: Show Flyway Database Migrations
Connect to PostgreSQL using `psql` or DBeaver:
```sql
-- Check Flyway schema history
SELECT * FROM flyway_schema_history;

-- Verify pgvector extension and tables
SELECT extname FROM pg_extension;
\dt
```
> *"Instead of JPA schema auto-generation (`ddl-auto`), we enforce Flyway version-controlled SQL migrations (`V1__init_schema.sql`). Notice that our `document_chunks` table includes a `vector(1536)` embedding column with an HNSW cosine distance index (`vector_cosine_ops`)."*

### Step 4: Show Swagger OpenAPI Documentation
Open in browser:
- Spring Boot Swagger: `http://localhost:8080/swagger-ui.html`
- FastAPI OpenAPI Docs: `http://localhost:8000/docs`

> *"Both services expose live, interactive OpenAPI 3.0 documentation. Spring Boot endpoints use global RFC 7807 problem details error responses and JWT Bearer security schemes."*
