from fastapi import Depends
from app.clients.groq_client import GroqClient
from app.providers.base_provider import BaseLLMProvider
from app.providers.groq_provider import GroqLLMProvider
from app.services.embedding_service import EmbeddingService, embedding_service
from app.services.text_extraction_service import TextExtractionService, text_extraction_service
from app.services.text_chunking_service import TextChunkingService, text_chunking_service
from app.services.document_ingestion_service import DocumentIngestionService
from app.retrieval.retriever import PgVectorRetriever
from app.prompting.prompt_builder import PromptBuilder
from app.services.llm_service import LLMService

_groq_client = GroqClient()
_groq_provider = GroqLLMProvider(_groq_client)
_retriever = PgVectorRetriever(embedding_service=embedding_service)
_prompt_builder = PromptBuilder()

def get_groq_client() -> GroqClient:
    return _groq_client

def get_llm_provider(client: GroqClient = Depends(get_groq_client)) -> BaseLLMProvider:
    return _groq_provider

def get_embedding_service() -> EmbeddingService:
    return embedding_service

def get_text_extraction_service() -> TextExtractionService:
    return text_extraction_service

def get_text_chunking_service() -> TextChunkingService:
    return text_chunking_service

def get_document_ingestion_service(
    extraction_service: TextExtractionService = Depends(get_text_extraction_service),
    chunking_service: TextChunkingService = Depends(get_text_chunking_service),
    emb_service: EmbeddingService = Depends(get_embedding_service)
) -> DocumentIngestionService:
    return DocumentIngestionService(
        extraction_service=extraction_service,
        chunking_service=chunking_service,
        embedding_service=emb_service
    )

def get_retriever() -> PgVectorRetriever:
    return _retriever

def get_prompt_builder() -> PromptBuilder:
    return _prompt_builder

def get_llm_service(
    provider: BaseLLMProvider = Depends(get_llm_provider),
    retriever: PgVectorRetriever = Depends(get_retriever),
    prompt_builder: PromptBuilder = Depends(get_prompt_builder)
) -> LLMService:
    return LLMService(
        provider=provider,
        retriever=retriever,
        prompt_builder=prompt_builder
    )
