import uuid
import logging
from app.core.config import settings
from app.schemas.chat import ChatRequest, ChatResponse, Citation
from app.providers.base_provider import BaseLLMProvider
from app.retrieval.retriever import PgVectorRetriever
from app.prompting.prompt_builder import PromptBuilder

logger = logging.getLogger("intelliflow.services.llm")

class LLMService:
    """
    Enterprise RAG & LLM Orchestrator Service.
    
    Coordinates candidate retrieval, prompt context assembly, LLM provider invocation,
    and structured completion synthesis with source citations.
    """

    def __init__(
        self,
        provider: BaseLLMProvider,
        retriever: PgVectorRetriever,
        prompt_builder: PromptBuilder
    ):
        self.provider = provider
        self.retriever = retriever
        self.prompt_builder = prompt_builder

    async def generate_chat_completion(
        self,
        request: ChatRequest,
        correlation_id: str = None
    ) -> ChatResponse:
        cid = correlation_id or str(uuid.uuid4())
        logger.info(f"[CorrelationID: {cid}] Starting Enterprise RAG Pipeline for prompt: '{request.prompt[:50]}...'")

        # 1. Candidate Vector Retrieval
        retrieved_chunks = await self.retriever.retrieve_similar_chunks(
            query=request.prompt,
            top_k=5
        )
        logger.info(f"[CorrelationID: {cid}] Retrieved {len(retrieved_chunks)} candidate chunks from pgvector index")

        # 2. Extract Citations
        citations = []
        for chunk in retrieved_chunks:
            citations.append(
                Citation(
                    document_id=chunk.get("document_id", "unknown-doc-id"),
                    document_title=chunk.get("document_title", "Corporate Document"),
                    chunk_index=chunk.get("chunk_index", 0),
                    content_snippet=chunk.get("content", "")[:150],
                    similarity_score=float(chunk.get("similarity_score", 0.0))
                )
            )

        # 3. Prompt Building & Context Assembly
        messages = self.prompt_builder.build_rag_messages(
            user_prompt=request.prompt,
            system_instruction=request.system_prompt,
            context_chunks=retrieved_chunks
        )

        # 4. LLM Provider Execution
        raw_result = await self.provider.generate_completion(
            messages=messages,
            temperature=request.temperature,
            max_tokens=request.max_tokens,
            correlation_id=cid
        )

        choices = raw_result.get("choices", [{}])
        choice_text = choices[0].get("message", {}).get("content", "No completion generated.")
        usage = raw_result.get("usage", {})

        return ChatResponse(
            response=choice_text,
            model=raw_result.get("model", settings.GROQ_MODEL_CHAT),
            prompt_tokens=usage.get("prompt_tokens", 0),
            completion_tokens=usage.get("completion_tokens", 0),
            total_tokens=usage.get("total_tokens", 0),
            citations=citations
        )
