# Section 03 Interview Questions: Advanced Tier

## Q1: How does Python's `asyncio` event loop handle concurrency, and what happens if a synchronous blocking operation is executed inside an `async def` route?
- **Ideal Answer**: Python's `asyncio` runs a single-threaded cooperative event loop. When an `async def` function executes an `awaitable` (like async DB or HTTP I/O), control yields back to the loop to process other tasks. However, if a developer executes a synchronous blocking function (e.g., `time.sleep()`, heavy CPU loops, or sync file I/O) inside an `async def` route, it **blocks the entire single-threaded event loop**, freezing processing for all concurrent users.
- **Fix**: Heavy CPU operations should be offloaded to thread/process pools via `asyncio.to_thread()` or Celery/Redis background workers.
- **Common Wrong Answer**: *"FastAPI automatically runs every function on a separate OS thread."*
- **Follow-up Question**: How does FastAPI handle plain `def` routes differently from `async def` routes? (FastAPI runs plain `def` routes in a background threadpool).
- **Interview Tip**: Contrast cooperative event loops vs threadpool execution.

## Q2: How do you configure Pytest for non-blocking async testing of FastAPI applications using `httpx.AsyncClient`?
- **Ideal Answer**: In `conftest.py`, we define a pytest fixture `async_client` using `httpx.AsyncClient(transport=ASGITransport(app=app), base_url="http://test")`. We mark test functions with `@pytest.mark.asyncio`. This allows tests to invoke FastAPI endpoints asynchronously without spinning up an external HTTP server, testing ASGI request/response cycles directly in memory.
- **Common Wrong Answer**: *"Use the standard synchronous Requests library inside pytest."*
- **Follow-up Question**: What is `ASGITransport`? (In-memory transport passing ASGI scope directly to FastAPI).
- **Interview Tip**: Highlight in-memory ASGI transport testing.
