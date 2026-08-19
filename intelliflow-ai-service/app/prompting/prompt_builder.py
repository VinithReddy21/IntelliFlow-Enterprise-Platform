from typing import List, Dict

class PromptBuilder:
    """
    Prompt Assembly Builder for Enterprise RAG pipelines.
    
    Structures retrieved document context passages into system and user message arrays.
    """

    @staticmethod
    def build_rag_messages(
        user_prompt: str,
        system_instruction: str,
        context_chunks: List[Dict[str, str]]
    ) -> List[Dict[str, str]]:
        context_payload_str = ""
        if context_chunks:
            passages = []
            for idx, chunk in enumerate(context_chunks, start=1):
                doc_title = chunk.get("document_title", "Corporate Document")
                content = chunk.get("content", "")
                passages.append(f"[Source {idx}: {doc_title}]\n{content}")
            context_payload_str = "\n\n".join(passages)
        else:
            context_payload_str = "No corporate document passages retrieved."

        full_system_prompt = (
            f"{system_instruction}\n\n"
            f"You are IntelliFlow Enterprise Copilot, a senior technical AI assistant. "
            f"When relevant enterprise knowledge documents and passages are provided below, prioritize and ground your answer on them with citations. "
            f"When asked general questions, technical concepts, architecture, or coding questions, provide comprehensive, natural, and accurate explanations while referencing relevant corporate context where applicable."
        )

        user_content = (
            f"Retrieved Knowledge Context:\n{context_payload_str}\n\n"
            f"User Question: {user_prompt}"
        )

        return [
            {"role": "system", "content": full_system_prompt},
            {"role": "user", "content": user_content}
        ]
