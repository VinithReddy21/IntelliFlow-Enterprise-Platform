import logging
from typing import List
from app.core.config import settings

logger = logging.getLogger("intelliflow.services.embedding")

class EmbeddingService:
    """
    Local Embedding Generation Service using sentence-transformers/all-MiniLM-L6-v2.
    
    Generates 384-dimensional dense float vector embeddings for semantic vector search.
    Strictly uses the SentenceTransformer model without synthetic or deterministic fallbacks.
    """

    def __init__(self):
        self.model_name = settings.EMBEDDING_MODEL_NAME
        self.dimension = settings.EMBEDDING_VECTOR_DIMENSION
        self._model = None

    def _load_model(self):
        if self._model is None:
            from sentence_transformers import SentenceTransformer
            logger.info(f"Loading SentenceTransformer model: {self.model_name}")
            self._model = SentenceTransformer(self.model_name)

    def encode_query(self, text: str) -> List[float]:
        if not text or not text.strip():
            return [0.0] * self.dimension

        self._load_model()
        embedding = self._model.encode(text, normalize_embeddings=True)
        return embedding.tolist()

    def is_model_loaded(self) -> bool:
        return self._model is not None

embedding_service = EmbeddingService()
