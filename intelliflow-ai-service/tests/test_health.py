import pytest

@pytest.mark.anyio
async def test_health_check_endpoint(async_client):
    """
    Test /health endpoint returns HTTP 200 and success ApiResponse JSON payload.
    """
    response = await async_client.get("/health")
    assert response.status_code == 200
    
    payload = response.json()
    assert payload["status"] == "success"
    assert payload["message"] == "AI Microservice is fully operational"
    assert payload["data"]["status"] == "healthy"
    assert "timestamp" in payload
