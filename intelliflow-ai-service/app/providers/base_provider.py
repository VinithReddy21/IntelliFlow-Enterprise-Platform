from abc import ABC, abstractmethod
from typing import Dict, Any, List

class BaseLLMProvider(ABC):
    """
    Abstract Base Class establishing the Provider Interface for LLM inference engines.
    """

    @abstractmethod
    async def generate_completion(
        self,
        messages: List[Dict[str, str]],
        temperature: float = 0.2,
        max_tokens: int = 1024,
        correlation_id: str = "N/A"
    ) -> Dict[str, Any]:
        """
        Generates chat completions given a message history payload.
        """
        pass
