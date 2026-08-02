from typing import Generic, TypeVar, Optional
from datetime import datetime, timezone
from pydantic import BaseModel, Field

T = TypeVar("T")

class ApiResponse(BaseModel, Generic[T]):
    """
    Standardized API Response Envelope for FastAPI endpoints.
    
    JSON Output Structure:
    {
      "status": "success",
      "message": "Operation completed successfully",
      "data": { ... },
      "timestamp": "2026-08-01T20:47:00Z"
    }
    """
    status: str = Field(default="success", description="Status flag: success or error")
    message: str = Field(default="Operation completed successfully", description="Human readable message")
    data: Optional[T] = Field(default=None, description="Generic response payload")
    timestamp: datetime = Field(default_factory=lambda: datetime.now(timezone.utc), description="UTC ISO timestamp")

    class Config:
        json_encoders = {
            datetime: lambda v: v.isoformat()
        }
