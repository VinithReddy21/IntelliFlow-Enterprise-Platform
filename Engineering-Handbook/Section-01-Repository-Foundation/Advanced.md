# Advanced Reading & Future Architecture Concepts

*Note: These concepts have not yet been implemented in Section 01. They serve as optional advanced reading for future project phases.*

---

## 1. Zero-Downtime Database Migrations (Expand-Contract Pattern)
In production systems, column renames or type changes must support Blue-Green deployments without locking tables:
1. **Expand**: Add a new nullable column (`V2__add_column.sql`).
2. **Dual-Write**: App writes to both old and new columns.
3. **Backfill**: Async job copies historical data.
4. **Contract**: Drop old column in a final release (`V3__drop_old_column.sql`).

---

## 2. Distributed Tracing & W3C TraceContext
When an API call spans Gateway -> Spring Boot -> FastAPI -> PostgreSQL, OpenTelemetry injects `traceparent` headers to trace requests across microservice network boundaries.

---

## 3. Kubernetes KEDA Event-Driven Autoscaling
For compute-heavy AI tasks (document vectorization), KEDA monitors Redis Stream queue length to auto-scale FastAPI worker pods dynamically based on pending workload depth.
