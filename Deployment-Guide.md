# IntelliFlow Enterprise Platform — Production Deployment Guide

---

## 1. Environment Architecture & Prerequisites

- **Docker Engine**: Version 24.0+
- **Docker Compose**: Version 2.20+
- **Database**: PostgreSQL 16 with `pgvector` extension
- **Cache**: Redis 7+
- **Target Cloud Providers**: AWS ECS / EKS, Azure Container Apps, DigitalOcean App Platform

---

## 2. Step-by-Step Deployment Flow

### Step 1: Clone Repository & Configure Environment
```bash
git clone https://github.com/intelliflow/intelliflow-platform.git
cd intelliflow-platform
cp .env.production.example .env
```
Edit `.env` to supply production database passwords, JWT secret, and OpenAI API credentials.

### Step 2: Build & Start Services using Docker Compose
```bash
docker compose up -d --build
```

### Step 3: Verify Container Health Probes
```bash
docker compose ps
curl http://localhost:8080/actuator/health
```

Expected Response:
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "aiService": { "status": "UP" },
    "storageService": { "status": "UP" }
  }
}
```

### Step 4: Verify Prometheus Metrics Endpoint
```bash
curl http://localhost:8080/actuator/prometheus
```

---

## 3. Production Readiness Checklist

- [x] Multi-stage Docker builds reducing image size to < 250 MB
- [x] Non-root container security execution (`intelliflow` UID 10001)
- [x] Zero plain-text credentials stored in source control
- [x] DB migrations verified via Flyway (`V1__init_schema.sql`)
- [x] HNSW index configured for 1536-dimensional vector search
- [x] Token bucket rate limiting active on Auth and AI routes
- [x] OWASP security response headers enabled
- [x] Distributed correlation tracing (`X-Correlation-ID`) in logs
