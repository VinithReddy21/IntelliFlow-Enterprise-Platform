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
    
    # AI Credentials
    OPENAI_API_KEY: str = Field(
        default="",
        description="OpenAI API authentication key"
    )
    OPENAI_MODEL_EMBEDDING: str = Field(
        default="text-embedding-3-small",
        description="Vector embedding model"
    )
    OPENAI_MODEL_CHAT: str = Field(
        default="gpt-4o-mini",
        description="Chat LLM model"
    )

    class Config:
        case_sensitive = True
        env_file = ".env"

settings = Settings()
