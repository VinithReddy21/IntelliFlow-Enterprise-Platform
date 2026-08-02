# Section 03: 1-Minute Cheat Sheet

```
+---------------------------------------------------------------------------------------------------+
|                        SECTION 03: FASTAPI AI SERVICE INFRASTRUCTURE CHEAT SHEET                 |
+---------------------------------------------------------------------------------------------------+
| CORE ENGINE         | Python 3.11 + FastAPI (ASGI Async Event Loop)                               |
| CONFIGURATION       | Pydantic v2 BaseSettings (app/core/config.py)                               |
| DATABASE PERSIST    | SQLAlchemy 2.0 Async Engine + asyncpg Driver (app/db/session.py)           |
| ERROR HANDLING      | Global JSON Exception Handlers -> ApiResponse[T] (app/core/exceptions.py)    |
| TESTING SUITE       | Pytest + HTTPX AsyncClient (conftest.py & tests/test_health.py)            |
+---------------------------------------------------------------------------------------------------+
| COMMANDS            | uvicorn main:app --reload              | Run FastAPI Dev Server (Port 8000)  |
|                     | pytest                                 | Run Async Python Test Suite         |
| DOCUMENTATION       | http://localhost:8000/docs             | Live Swagger / OpenAPI Interactive UI|
+---------------------------------------------------------------------------------------------------+
```
