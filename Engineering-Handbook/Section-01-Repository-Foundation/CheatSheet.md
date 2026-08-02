# Section 01: 1-Minute Cheat Sheet

```
+---------------------------------------------------------------------------------------------------+
|                              SECTION 01: REPOSITORY FOUNDATION CHEAT SHEET                        |
+---------------------------------------------------------------------------------------------------+
| CORE ARCHITECTURE   | Java 21 Spring Boot 3.2 (Core) + Python 3.11 FastAPI (AI Microservice)       |
| DATA & STORAGE      | PostgreSQL 16 + pgvector (1536-dim HNSW Cosine Index) | Flyway Migrations |
| CACHE / SECURITY    | Redis 7.2 (Token Blacklist, Sliding Window Rate Limiting) | Dual-Token RS256  |
+---------------------------------------------------------------------------------------------------+
| LOCAL COMMANDS      | docker-compose up -d                   | Launch Postgres, Redis, App Services|
|                     | mvn clean package -DskipTests          | Compile Spring Boot Jar Artifact   |
|                     | pytest                                 | Run FastAPI Python Test Suite     |
+---------------------------------------------------------------------------------------------------+
| GOVERNANCE METADATA | .gitignore                             | Secures .env secrets & binaries   |
|                     | README.md                              | Quickstart, badges, architecture   |
|                     | CONTRIBUTING.md                        | Branch naming & conventional commits|
|                     | CHANGELOG.md                           | SemVer release tracking            |
|                     | docs/engineering-decisions/            | Architecture Decision Records     |
+---------------------------------------------------------------------------------------------------+
```
