import logging
from typing import List, Dict, Any

logger = logging.getLogger("intelliflow.services.chunking")

class TextChunkingService:
    """
    Configurable Text Chunking Service for Enterprise RAG Document Ingestion.
    """

    def chunk_text(
        self,
        text: str,
        chunk_size: int = 500,
        chunk_overlap: int = 50
    ) -> List[Dict[str, Any]]:
        if not text or not text.strip():
            return []

        words = text.split()
        chunks = []
        start = 0
        chunk_index = 0

        while start < len(words):
            end = min(start + chunk_size, len(words))
            chunk_words = words[start:end]
            chunk_text = " ".join(chunk_words)

            chunks.append({
                "chunk_index": chunk_index,
                "content": chunk_text,
                "token_count": len(chunk_words)
            })

            chunk_index += 1
            start += (chunk_size - chunk_overlap)
            if start >= len(words):
                break

        logger.info(f"Chunked document into {len(chunks)} passages (Size: {chunk_size}, Overlap: {chunk_overlap})")
        return chunks

text_chunking_service = TextChunkingService()
