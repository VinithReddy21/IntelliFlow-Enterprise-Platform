from typing import Dict, Any, List
from app.providers.base_provider import BaseLLMProvider
from app.clients.groq_client import GroqClient

class GroqLLMProvider(BaseLLMProvider):
    """
    Concrete Provider implementation wrapping Groq Client communications.
    """

    def __init__(self, client: GroqClient):
        self.client = client

    async def generate_completion(
        self,
        messages: List[Dict[str, str]],
        temperature: float = 0.2,
        max_tokens: int = 1024,
        correlation_id: str = "N/A"
    ) -> Dict[str, Any]:
        return await self.client.create_chat_completion(
            messages=messages,
            temperature=temperature,
            max_tokens=max_tokens,
            correlation_id=correlation_id
        )
