# FastAPI & Python AI Microservice Interview Knowledge Base

## Q1: Why did we select Python FastAPI for the AI microservice instead of running Python scripts directly inside Spring Boot?

### Level 1 — Campus Placement Answer
> "FastAPI is a fast Python web framework. We use it to build an AI microservice because Python has great libraries for AI like OpenAI and PyMuPDF, while Spring Boot handles our main database and user endpoints."

### Level 2 — Product Company Answer
> "Running Python scripts directly from Java using `ProcessBuilder` or Jython is slow, inefficient, and creates memory leaks. Python FastAPI provides an asynchronous ASGI web server (built on Starlette) that communicates with Spring Boot over HTTP/REST or gRPC. FastAPI uses Pydantic v2 for automatic request validation, type checking, and dynamic OpenAPI generation, allowing us to build high-performance async AI pipelines."

### Level 3 — Senior Engineer Answer
> "Isolating AI workloads into a dedicated FastAPI microservice protects our core Java domain from Python dependency pollution and heavy C-extension memory footprints (PyMuPDF, PyTorch, SentenceTransformers). FastAPI's async/await execution model is ideal for I/O-bound LLM API calls and streaming HTTP responses. It allows us to scale AI compute pods independently using Kubernetes KEDA based on Redis queue depths without affecting main platform transactional throughput."
