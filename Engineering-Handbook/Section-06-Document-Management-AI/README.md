# Section 06: Enterprise Document Management & AI Knowledge Engine (Step 7 REST API Layer)

---

## 1. Prerequisites

Before reading this section, you should understand:
- OpenAPI / Swagger 3.0 documentation annotations.
- Standardized `ApiResponse<T>` wrappers.
- `Authentication` parameter injection in Spring MVC.
- Multipart File Upload (`@RequestPart("file")` and `@RequestPart("data")`).

---

## 2. Learning Objectives

After completing Step 7, you will master:
- **`DocumentController`**: Production REST API endpoints exposing document ingestion, file downloads, vector similarity search, and grounded RAG answer generation.
- **Thin Controller Pattern**: Delegating all orchestration and security business rules to `DocumentService` and `RagSearchEngineService`.
- **Security & Authorization**: Applying `@PreAuthorize("hasAnyRole('USER', 'ADMIN')")` and extracting authenticated user UUIDs safely without `SecurityContextHolder` direct coupling.

---

## 3. REST API Endpoint Catalog

| Endpoint | Method | Path | Security | Description |
| :--- | :--- | :--- | :--- | :--- |
| Upload Document | `POST` | `/api/v1/documents` | `ROLE_USER`, `ROLE_ADMIN` | Uploads binary document file, parses text, chunks tokens, and triggers async vector embedding |
| Get Document | `GET` | `/api/v1/documents/{id}` | `ROLE_USER`, `ROLE_ADMIN` | Retrieves document metadata by ID |
| Get Details | `GET` | `/api/v1/documents/{id}/details` | `ROLE_USER`, `ROLE_ADMIN` | Retrieves document metadata with constituent chunk snippet projections |
| Download File | `GET` | `/api/v1/documents/{id}/download` | `ROLE_USER`, `ROLE_ADMIN` | Streams raw file content with attachment `Content-Disposition` |
| List Documents | `GET` | `/api/v1/documents` | `ROLE_USER`, `ROLE_ADMIN` | Paginated query with optional department filtering |
| Soft Delete | `DELETE` | `/api/v1/documents/{id}` | `ROLE_USER`, `ROLE_ADMIN` | Marks document soft-deleted (`deletedAt`) |
| Similarity Search | `POST` | `/api/v1/documents/search/similarity` | `ROLE_USER`, `ROLE_ADMIN` | Native HNSW pgvector similarity search over chunk embeddings |
| RAG Query | `POST` | `/api/v1/documents/search/rag` | `ROLE_USER`, `ROLE_ADMIN` | Grounded AI answer generation with explicit source citations |
