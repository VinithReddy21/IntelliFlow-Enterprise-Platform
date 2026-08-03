from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.exceptions import RequestValidationError

from app.core.config import settings
from app.core.exceptions import (
    CustomBaseException,
    custom_base_exception_handler,
    validation_exception_handler,
    global_exception_handler
)
from app.api.v1.api import api_router
from app.schemas.response import ApiResponse

app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.VERSION,
    openapi_url=f"{settings.API_V1_STR}/openapi.json",
    docs_url="/docs",
    redoc_url="/redoc"
)

# CORS Middleware Setup
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Exception Handler Registrations
app.add_exception_handler(CustomBaseException, custom_base_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)
app.add_exception_handler(Exception, global_exception_handler)

# Register API Routers under /api/v1
app.include_router(api_router, prefix=settings.API_V1_STR)

# Root Level Health Check for Render & Kubernetes Liveness Probes
@app.get("/health", tags=["Health Probe"], response_model=ApiResponse[dict])
async def root_health_check():
    """
    Root level health probe endpoint.
    """
    return ApiResponse[dict](
        status="success",
        message="AI Microservice is fully operational",
        data={
            "service": settings.PROJECT_NAME,
            "version": settings.VERSION,
            "status": "healthy"
        }
    )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
