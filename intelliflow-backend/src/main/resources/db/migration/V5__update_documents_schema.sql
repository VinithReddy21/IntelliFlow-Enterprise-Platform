-- Flyway V5 Migration: Align documents table schema with DocumentEntity domain model

-- 1. Add missing columns to documents table
ALTER TABLE documents
ADD COLUMN IF NOT EXISTS title VARCHAR(255),
ADD COLUMN IF NOT EXISTS checksum_sha256 VARCHAR(64),
ADD COLUMN IF NOT EXISTS file_size_bytes BIGINT,
ADD COLUMN IF NOT EXISTS entity_type VARCHAR(50),
ADD COLUMN IF NOT EXISTS entity_id UUID,
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- 2. Backfill from legacy columns if rows exist
UPDATE documents SET checksum_sha256 = checksum WHERE checksum_sha256 IS NULL AND checksum IS NOT NULL;
UPDATE documents SET file_size_bytes = size_bytes WHERE file_size_bytes IS NULL AND size_bytes IS NOT NULL;
UPDATE documents SET title = file_name WHERE title IS NULL AND file_name IS NOT NULL;

-- 3. Apply NOT NULL constraints for required fields
ALTER TABLE documents
ALTER COLUMN title SET NOT NULL,
ALTER COLUMN checksum_sha256 SET NOT NULL,
ALTER COLUMN file_size_bytes SET NOT NULL;

-- 4. Expand file_key length to VARCHAR(512)
ALTER TABLE documents
ALTER COLUMN file_key TYPE VARCHAR(512);

-- 5. Create indexes matching DocumentEntity definitions
CREATE UNIQUE INDEX IF NOT EXISTS idx_documents_file_key ON documents(file_key);
CREATE INDEX IF NOT EXISTS idx_documents_uploader_id ON documents(uploader_id);
CREATE INDEX IF NOT EXISTS idx_documents_department_id ON documents(department_id);
CREATE INDEX IF NOT EXISTS idx_documents_entity ON documents(entity_type, entity_id);
