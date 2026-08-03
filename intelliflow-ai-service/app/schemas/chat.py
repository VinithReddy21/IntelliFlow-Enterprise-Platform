from typing import Optional, List
from pydantic import BaseModel, Field

class ChatMessage(BaseModel):
    """
    Individual chat message representation.
    """
    role: str = Field(..., description="Message sender role: system, user, or assistant")
    content: str = Field(..., description="Text content of the message")

class Citation(BaseModel):
    """
    Grounded source citation item for RAG answer verification.
    """
    document_id: str = Field(..., description="Source document UUID")
    document_title: str = Field(..., description="Source document file name")
    chunk_index: int = Field(..., description="Zero-indexed chunk position in document")
    content_snippet: str = Field(..., description="Extracted text snippet from chunk")
    similarity_score: float = Field(..., description="Cosine similarity score (0.0 to 1.0)")

class ChatRequest(BaseModel):
    """
    Request model for POST /api/v1/chat.
    """
    prompt: str = Field(..., min_length=1, description="Primary user prompt or question")
    system_prompt: Optional[str] = Field(
        default="You are IntelliFlow Copilot, an enterprise AI assistant.",
        description="Optional system prompt to guide LLM behavior"
    )
    temperature: Optional[float] = Field(
        default=0.2,
        ge=0.0,
        le=1.0,
        description="LLM sampling temperature (0.0 = deterministic, 1.0 = creative)"
    )
    max_tokens: Optional[int] = Field(
        default=1024,
        ge=1,
        le=4096,
        description="Maximum tokens to generate"
    )
    messages: Optional[List[ChatMessage]] = Field(
        default=None,
        description="Optional multi-turn conversation history"
    )

class ChatResponse(BaseModel):
    """
    Response model for POST /api/v1/chat.
    """
    response: str = Field(..., description="LLM generated answer text")
    model: str = Field(..., description="Model identifier used for inference")
    prompt_tokens: int = Field(default=0, description="Tokens used in input prompt")
    completion_tokens: int = Field(default=0, description="Tokens generated in response")
    total_tokens: int = Field(default=0, description="Total tokens consumed")
    citations: Optional[List[Citation]] = Field(
        default=None,
        description="Grounded document source citations used for context assembly"
    )
