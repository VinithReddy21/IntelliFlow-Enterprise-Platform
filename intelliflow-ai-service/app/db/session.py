from typing import AsyncGenerator
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession, async_sessionmaker
from app.core.config import settings

async_engine = None
AsyncSessionFactory = None

if settings.DATABASE_URL and settings.DATABASE_URL.startswith("postgresql"):
    async_engine = create_async_engine(
        settings.DATABASE_URL,
        echo=False,
        future=True,
        pool_size=10,
        max_overflow=20,
        pool_pre_ping=True
    )
    AsyncSessionFactory = async_sessionmaker(
        bind=async_engine,
        class_=AsyncSession,
        expire_on_commit=False,
        autocommit=False,
        autoflush=False
    )

async def get_db() -> AsyncGenerator[AsyncSession, None]:
    """
    Dependency generator yielding an async database session per request.
    Ensures sessions are cleanly closed after request completion.
    """
    if AsyncSessionFactory is None:
        yield None
        return

    async with AsyncSessionFactory() as session:
        try:
            yield session
            await session.commit()
        except Exception:
            await session.rollback()
            raise
        finally:
            await session.close()
