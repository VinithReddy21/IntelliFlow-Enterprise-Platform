import time
import logging
import httpx
from typing import Dict, Any, List
from app.core.config import settings

logger = logging.getLogger("intelliflow.clients.groq")

class GroqClient:
    """
    HTTP Client encapsulating all direct API communications with Groq's OpenAI-compatible service.
    
    Includes structured logging for correlation tracking, model latency, and token metrics.
    """

    def __init__(self):
        self.base_url = settings.GROQ_BASE_URL.rstrip('/')
        self.api_key = settings.GROQ_API_KEY
        self.default_model = settings.GROQ_MODEL_CHAT

    async def create_chat_completion(
        self,
        messages: List[Dict[str, str]],
        temperature: float = 0.2,
        max_tokens: int = 1024,
        correlation_id: str = "N/A"
    ) -> Dict[str, Any]:
        start_time = time.perf_counter()
        
        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
            "X-Correlation-ID": correlation_id
        }

        payload = {
            "model": self.default_model,
            "messages": messages,
            "temperature": temperature,
            "max_tokens": max_tokens
        }

        logger.info(
            f"[CorrelationID: {correlation_id}] Dispatching LLM request to Groq | Provider: Groq | "
            f"Model: {self.default_model} | MessageCount: {len(messages)}"
        )

        if not self.api_key or not self.api_key.startswith("gsk_"):
            logger.warning(f"[CorrelationID: {correlation_id}] No valid GROQ_API_KEY configured. Returning fallback response.")
            return {
                "choices": [{
                    "message": {
                        "content": f"IntelliFlow Copilot Enterprise [Groq / {self.default_model}]: "
                                   f"Grounded response generated for prompt."
                    }
                }],
                "usage": {"prompt_tokens": 15, "completion_tokens": 25, "total_tokens": 40},
                "model": self.default_model
            }

        async with httpx.AsyncClient(timeout=30.0) as client:
            endpoint_url = f"{self.base_url}/chat/completions"
            response = await client.post(endpoint_url, headers=headers, json=payload)
            latency_ms = round((time.perf_counter() - start_time) * 1000, 2)

            if response.status_code == 200:
                data = response.json()
                usage = data.get("usage", {})
                logger.info(
                    f"[CorrelationID: {correlation_id}] Groq request success | Latency: {latency_ms}ms | "
                    f"PromptTokens: {usage.get('prompt_tokens', 0)} | "
                    f"CompletionTokens: {usage.get('completion_tokens', 0)} | "
                    f"TotalTokens: {usage.get('total_tokens', 0)}"
                )
                return data
            else:
                logger.error(
                    f"[CorrelationID: {correlation_id}] Groq API HTTP Error {response.status_code} | "
                    f"Latency: {latency_ms}ms | Response: {response.text}"
                )
                response.raise_for_status()
                return {}
