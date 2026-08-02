# Section 03 Interview Questions: Beginner Tier

## Q1: Why did we select FastAPI over Flask for the AI microservice?
- **Ideal Answer**: FastAPI is built on ASGI (Starlette) which natively supports asynchronous non-blocking I/O (`async`/`await`). It automatically validates inputs using Pydantic and generates OpenAPI Swagger documentation out of the box. Flask is WSGI-based and synchronous by default.
- **Common Wrong Answer**: *"Because Flask is outdated and cannot run Python 3."*
- **Follow-up Question**: What server runs FastAPI applications in production? (Uvicorn / Hypercorn).
- **Interview Tip**: Mention async execution and automatic OpenAPI documentation.

## Q2: How does Pydantic validate data in FastAPI?
- **Ideal Answer**: Pydantic uses standard Python type hints to parse and validate incoming JSON request payloads at runtime. If data types match, Pydantic converts JSON into strongly typed Python objects; if invalid, it raises a `RequestValidationError`.
- **Common Wrong Answer**: *"Pydantic checks data by querying the PostgreSQL database."*
- **Follow-up Question**: What version of Pydantic is used, and why is it faster? (Pydantic v2 uses Rust core compiled bindings).
- **Interview Tip**: Highlight runtime type enforcement.

## Q3: What is the purpose of `app/core/config.py`?
- **Ideal Answer**: `app/core/config.py` uses Pydantic's `BaseSettings` to load environment variables from `.env` files into a single, validated application settings object, providing default fallback values for local development.
- **Common Wrong Answer**: *"It configures database tables."*
- **Follow-up Question**: Why inherit from `BaseSettings` instead of reading `os.environ` directly? (Type safety and validation).
- **Interview Tip**: Emphasize 12-Factor App configuration isolation.
