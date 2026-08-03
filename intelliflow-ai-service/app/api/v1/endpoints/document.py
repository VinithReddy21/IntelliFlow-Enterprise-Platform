from fastapi import APIRouter, Depends, File, UploadFile, status
from app.schemas.document import IngestionResponse
from app.schemas.response import ApiResponse
from app.services.document_ingestion_service import DocumentIngestionService
from app.dependencies import get_document_ingestion_service

document_router = APIRouter()

@document_router.post(
    "/documents/ingest",
    tags=["Document Ingestion"],
    response_model=ApiResponse[IngestionResponse],
    status_code=status.HTTP_201_CREATED
)
async def ingest_document(
    file: UploadFile = File(...),
    ingestion_service: DocumentIngestionService = Depends(get_document_ingestion_service)
):
    """
    Ingests PDF, DOCX, TXT, or Markdown document files.
    
    Parses raw text, splits into overlapping chunks, generates 384-dim vector embeddings,
    and persists document metadata and vectors to PostgreSQL + pgvector.
    """
    file_bytes = await file.read()
    mime_type = file.content_type or "application/octet-stream"
    
    result = await ingestion_service.ingest_document(
        file_bytes=file_bytes,
        filename=file.filename,
        mime_type=mime_type
    )

    return ApiResponse[IngestionResponse](
        status="success",
        message="Document successfully processed and vectorized",
        data=result
    )
