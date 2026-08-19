-- V9__update_embedding_vector_dimension.sql
-- Update document_chunks embedding column type from vector(1536) to vector(384) for sentence-transformers/all-MiniLM-L6-v2 compatibility

ALTER TABLE document_chunks ALTER COLUMN embedding TYPE vector(384);
