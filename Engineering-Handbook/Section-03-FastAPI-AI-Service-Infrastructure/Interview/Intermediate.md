# Section 03 Interview Questions: Intermediate Tier

## Q1: How does async connection pooling work in SQLAlchemy 2.0 with `asyncpg`?
- **Ideal Answer**: We initialize an async engine using `create_async_engine("postgresql+asyncpg://...")` with `pool_size=10` and `max_overflow=20`. The engine maintains pre-established non-blocking TCP database connections. In FastAPI, `get_db()` is an async generator dependency that yields an `AsyncSession` per request using `async with`, ensuring sessions commit or rollback and close cleanly without blocking the event loop.
- **Common Wrong Answer**: *"Every request creates a brand new TCP database connection."*
- **Follow-up Question**: Why use `asyncpg` instead of `psycopg2`? (`asyncpg` is written specifically for Python `asyncio` non-blocking socket I/O).
- **Interview Tip**: Explain session acquisition, yield, commit/rollback, and cleanup.

## Q2: How do global exception handlers work in FastAPI?
- **Ideal Answer**: We register custom exception handlers on the FastAPI app instance using `app.add_exception_handler(ExceptionClass, handler_function)`. When an exception is raised (e.g. `CustomBaseException` or `RequestValidationError`), the handler intercepts it, formats the error details into a standardized `ApiResponse` payload, and returns an HTTP `JSONResponse` with appropriate status codes (e.g. 400, 422, 500).
- **Common Wrong Answer**: *"Exceptions must be caught using try-catch inside every single endpoint function."*
- **Follow-up Question**: What HTTP status code does FastAPI return for validation errors by default? (`422 Unprocessable Entity`).
- **Interview Tip**: Emphasize clean controller code and standardized error envelopes.
