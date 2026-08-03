from pydantic_settings import BaseSettings
from pydantic import Field

class Settings(BaseSettings):
    """
    Application Settings powered by Pydantic v2.
    
    Reads configuration strictly from environment variables.
    """
    PROJECT_NAME: str = Field(default="IntelliFlow AI Service", description="Name of the AI microservice")
    VERSION: str = Field(default="1.0.0", description="Application semantic version")
    API_V1_STR: str = Field(default="/api/v1", description="API route prefix")
    
    # Infrastructure Connections
    DATABASE_URL: str = Field(
        default="",
        description="Async PostgreSQL connection string using asyncpg driver"
    )
    REDIS_URL: str = Field(
        default="",
        description="Redis connection URL"
    )
    
    # AI Credentials & Model Configuration (Groq LLM + SentenceTransformers)
    GROQ_API_KEY: str = Field(
        default="",
        description="Groq API authentication key"
    )
    GROQ_BASE_URL: str = Field(
        default="https://api.groq.com/openai/v1",
        description="Groq OpenAI-compatible API base URL"
    )
    GROQ_MODEL_CHAT: str = Field(
        default="llama-3.3-70b-versatile",
        description="Groq free LLM model"
    )
    EMBEDDING_MODEL_NAME: str = Field(
        default="sentence-transformers/all-MiniLM-L6-v2",
        description="Hugging Face Sentence Transformer embedding model"
    )
    EMBEDDING_VECTOR_DIMENSION: int = Field(
        default=384,
        description="Vector dimension produced by sentence-transformers/all-MiniLM-L6-v2"
    )

    class Config:
        case_sensitive = True
        env_file = ".env"

settings = Settings()
