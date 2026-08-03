import io
import pytest

@pytest.mark.anyio
async def test_document_ingestion_endpoint(async_client):
    file_content = b"IntelliFlow Enterprise Platform Architecture. This is a production document ingestion test."
    files = {
        "file": ("test_architecture.txt", io.BytesIO(file_content), "text/plain")
    }

    response = await async_client.post("/api/v1/documents/ingest", files=files)
    assert response.status_code == 201
    
    data = response.json()
    assert data["status"] == "success"
    assert "data" in data
    assert data["data"]["file_name"] == "test_architecture.txt"
    assert data["data"]["total_chunks"] >= 1
    assert "document_id" in data["data"]
    assert "checksum" in data["data"]
