import pytest

@pytest.mark.anyio
async def test_chat_endpoint_success(async_client):
    payload = {
        "prompt": "What is the vector similarity search index standard?",
        "temperature": 0.2,
        "max_tokens": 512
    }
    response = await async_client.post("/api/v1/chat", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "success"
    assert "data" in data
    assert "response" in data["data"]
    assert "model" in data["data"]
    assert "citations" in data["data"]
    assert len(data["data"]["citations"]) > 0
    assert "document_title" in data["data"]["citations"][0]
