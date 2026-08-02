# Section 03: FastAPI AI Service Infrastructure

---

## 1. Prerequisites

Before reading this section, you should understand:
- Python 3.11 async/await syntax (`async def`, `await`).
- HTTP REST API basics (`GET`, `POST`, status codes).
- High-level concepts of Object-Relational Mapping (ORM) and connection pooling.

---

## 2. Learning Objectives

After completing Section 03, you will master:
- **Asynchronous ASGI Architecture**: Why FastAPI on Uvicorn outperforms synchronous WSGI frameworks for I/O-bound microservices.
- **Pydantic v2 Settings & Validation**: How Rust-backed parsing enforces environment configuration and validates JSON schemas.
- **Async Database Connection Pooling**: How `SQLAlchemy 2.0` with `asyncpg` manages non-blocking PostgreSQL connections.
- **Global Exception Interceptors**: Translating Python domain exceptions into standardized JSON API envelopes (`ApiResponse`).
- **Async Pytest Suite**: Writing non-blocking integration tests for FastAPI using `httpx.AsyncClient`.

---

## 3. Implementation Checklist

Verify that you understand every implemented Section 03 component:
- [x] **Pydantic Settings**: `app/core/config.py` loading `.env` variables via `BaseSettings`.
- [x] **Async Session Factory**: `app/db/session.py` using `create_async_engine`, `async_sessionmaker`, and `get_db()`.
- [x] **Global Exception Handling**: `app/core/exceptions.py` capturing custom and validation errors.
- [x] **Unified JSON Response**: `app/schemas/response.py` providing `ApiResponse[T]` envelope.
- [x] **Main Application**: `main.py` with CORS middleware, health probe `/health`, and OpenAPI metadata.
- [x] **Pytest Testing Suite**: `conftest.py` and `tests/test_health.py` validating endpoints async.

---

## 4. Deep Engineering Concepts

### Concept 1: Asynchronous Event Loop (`async`/`await`)
- **WHAT**: A single-threaded cooperative multitasking mechanism managed by Python's `asyncio` loop.
- **WHY**: Synchronous frameworks block an OS thread during network/database I/O. Async functions yield control back to the event loop during I/O wait times, allowing a single process to handle thousands of concurrent requests.
- **WHEN**: Essential for I/O-bound microservices making external HTTP calls (like LLM APIs) or database queries.
- **HOW**: `async def` defines a coroutine; `await` pauses execution until the awaited awaitable completes.
- **ADVANTAGES**: Ultra-low memory usage compared to spawning OS threads.
- **LIMITATIONS**: CPU-bound tasks (like heavy mathematical loops) will block the single event loop unless offloaded to process pools.

---

### Concept 2: Async Connection Pooling (`SQLAlchemy 2.0` + `asyncpg`)
- **WHAT**: A pool of pre-established database connections maintained asynchronously.
- **WHY**: Opening a new TCP connection and performing PostgreSQL authentication per request adds ~20–50ms latency. Pooling reuses existing open connections.
- **HOW**: `create_async_engine` uses `pool_size=10` and `max_overflow=20`. FastAPI's `get_db()` dependency acquires a session asynchronously via `async with` and releases it in `finally`.
- **ADVANTAGES**: Eliminates connection overhead and protects PostgreSQL from connection exhaustion.

---

## 5. Technology Choices Justification Matrix

| Tech Choice | Implemented Purpose | Alternatives Considered | Core Advantage | Limitation |
| :--- | :--- | :--- | :--- | :--- |
| **FastAPI** | Asynchronous AI Microservice API | Flask, Django | High-performance ASGI event loop + Pydantic v2 | CPU-intensive loops block single event loop |
| **Pydantic v2** | Environment & Request validation | Marshmallow, dataclasses | Rust-backed `pydantic-core` parsing; automatic OpenAPI generation | Strict type parsing requires precise schemas |
| **SQLAlchemy 2.0 + asyncpg** | Non-blocking database session pool | psycopg2 (sync), Peewee | True non-blocking async PostgreSQL I/O | Async ORM syntax is slightly more verbose |
| **HTTPX AsyncClient** | Non-blocking Pytest client | Requests (sync) | Enables native `pytest-asyncio` testing for ASGI apps | Requires async fixture setup |

---

## 6. Industry Perspective

- **Startups**: Use FastAPI for rapid MVP development of AI and microservice endpoints due to native type hints and built-in Swagger `/docs`.
- **Product Companies**: Enforce Pydantic v2 schemas across all microservices to prevent invalid data from penetrating domain layers.
- **FAANG Enterprises**: Isolate heavy Python ML workloads into dedicated microservices (like our `intelliflow-ai-service`), keeping main transactional cores (Java/Spring Boot) decoupled.
