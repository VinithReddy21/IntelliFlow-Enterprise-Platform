import logging
import math
import hashlib
from typing import List
from app.core.config import settings

logger = logging.getLogger("intelliflow.services.embedding")

class EmbeddingService:
    """
    Local Embedding Generation Service using sentence-transformers/all-MiniLM-L6-v2.
    
    Generates 384-dimensional dense float vector embeddings for semantic vector search.
    """

    def __init__(self):
        self.model_name = settings.EMBEDDING_MODEL_NAME
        self.dimension = settings.EMBEDDING_VECTOR_DIMENSION
        self._model = None

    def _load_model(self):
        if self._model is None:
            try:
                from sentence_transformers import SentenceTransformer
                logger.info(f"Loading SentenceTransformer model: {self.model_name}")
                self._model = SentenceTransformer(self.model_name)
            except Exception as e:
                logger.warning(f"Could not load SentenceTransformer ({str(e)}). Using deterministic 384-dim fallback vector encoder.")
                self._model = "FALLBACK"

    def encode_query(self, text: str) -> List[float]:
        if not text or not text.strip():
            return [0.0] * self.dimension

        self._load_model()

        if self._model != "FALLBACK":
            try:
                embedding = self._model.encode(text, normalize_embeddings=True)
                return embedding.tolist()
            except Exception as e:
                logger.error(f"SentenceTransformer encoding failed: {str(e)}. Falling back to deterministic vector.")

        return self._compute_deterministic_vector(text, self.dimension)

    def _compute_deterministic_vector(self, text: str, dim: int) -> List[float]:
        hash_digest = hashlib.sha256(text.encode('utf-8')).digest()
        vector = []
        sum_sq = 0.0
        for i in range(dim):
            b = hash_digest[i % len(hash_digest)]
            val = math.sin(float(b) + float(i))
            vector.append(val)
            sum_sq += val * val

        norm = math.sqrt(sum_sq)
        if norm > 0:
            vector = [v / norm for v in vector]
        return vector

embedding_service = EmbeddingService()
