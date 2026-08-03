from fastapi import APIRouter
from app.api.v1.endpoints.health import health_router
from app.api.v1.endpoints.chat import chat_router
from app.api.v1.endpoints.document import document_router

api_router = APIRouter()

# Register V1 Endpoints
api_router.include_router(health_router)
api_router.include_router(chat_router)
api_router.include_router(document_router)
