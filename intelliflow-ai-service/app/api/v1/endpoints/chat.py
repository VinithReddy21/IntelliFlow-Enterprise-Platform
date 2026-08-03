import uuid
from typing import Optional
from fastapi import APIRouter, Depends, Header, status
from app.schemas.chat import ChatRequest, ChatResponse
from app.schemas.response import ApiResponse
from app.services.llm_service import LLMService
from app.dependencies import get_llm_service

chat_router = APIRouter()

@chat_router.post(
    "/chat",
    tags=["AI Chat"],
    response_model=ApiResponse[ChatResponse],
    status_code=status.HTTP_200_OK
)
async def chat_completion(
    request: ChatRequest,
    x_correlation_id: Optional[str] = Header(None, alias="X-Correlation-ID"),
    llm_service: LLMService = Depends(get_llm_service)
):
    """
    Executes Enterprise RAG Chat Completion using LLM inference service and vector retrieval.
    
    Accepts user prompt and optional system prompts, returning grounded LLM answers.
    """
    cid = x_correlation_id or str(uuid.uuid4())
    result = await llm_service.generate_chat_completion(request, correlation_id=cid)
    return ApiResponse[ChatResponse](
        status="success",
        message="Enterprise RAG chat completion generated successfully",
        data=result
    )
