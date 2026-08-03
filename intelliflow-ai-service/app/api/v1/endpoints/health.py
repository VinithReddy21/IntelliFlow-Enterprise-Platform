from fastapi import APIRouter
from app.core.config import settings
from app.schemas.response import ApiResponse

health_router = APIRouter()

@health_router.get("/health", tags=["Health Probe"], response_model=ApiResponse[dict])
async def health_check():
    """
    Health check endpoint for Kubernetes & Render liveness & readiness probes.
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
