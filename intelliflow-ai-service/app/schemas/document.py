from typing import Optional, List
from pydantic import BaseModel, Field

class IngestionResponse(BaseModel):
    """
    Response payload for document ingestion pipeline.
    """
    document_id: str = Field(..., description="Unique document UUID")
    file_name: str = Field(..., description="Uploaded document file name")
    file_size_bytes: int = Field(..., description="File size in bytes")
    checksum: str = Field(..., description="SHA-256 document checksum")
    total_chunks: int = Field(..., description="Number of text chunks created")
    total_tokens: int = Field(..., description="Total estimated token count across chunks")
    status: str = Field(default="ACTIVE", description="Ingestion processing status")
    message: str = Field(default="Document successfully ingested and vectorized", description="Status message")
