# ADR-003: Selection of Python FastAPI for AI Engine Microservice

## Status
**ACCEPTED**

## Context
IntelliFlow AI incorporates automated document intelligence, PDF parsing, meeting transcript summarization, and RAG retrieval pipelines. A dedicated service is needed to interface cleanly with AI frameworks (LangChain, OpenAI, PyMuPDF, sentence-transformers).

## Alternatives Considered

### 1. Python Flask / Django
- **Pros**: Mature ecosystems.
- **Cons**: WSGI architecture is natively synchronous; handling async streaming responses or high-concurrency LLM HTTP requests requires complex multi-process WSGI wrapper configurations.

### 2. Embedded Python inside Java (Jython / ProcessBuilder)
- **Pros**: Keeps code within a single runtime.
- **Cons**: Poor execution performance; memory leaks; pollutes Java application threads with heavy ML dependencies.

### 3. Python FastAPI (SELECTED)
- **Pros**: ASGI asynchronous architecture (built on Starlette); native Pydantic v2 type checking and schema enforcement; dynamic generation of OpenAPI `/docs`; native integration with the Python AI/ML ecosystem.
- **Cons**: Dynamic language requires strict discipline and type hints (`mypy`/Pydantic) to prevent runtime type errors.

## Decision
We select **Python FastAPI** as the isolated AI microservice engine.

## Interview Defense & Key Summary
> *"FastAPI allows us to isolate heavy ML dependencies (PyMuPDF, LangChain, vector math) inside a Python microservice while providing async ASGI performance for streaming LLM responses. This protects our Java core backend from Python dependency bloat."*
