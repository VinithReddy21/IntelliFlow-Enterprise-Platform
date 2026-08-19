-- V8__remove_legacy_document_columns.sql
-- Safely drop legacy obsolete columns on documents table that conflict with DocumentEntity JPA mapping

ALTER TABLE documents DROP COLUMN IF EXISTS file_name;
ALTER TABLE documents DROP COLUMN IF EXISTS checksum;
ALTER TABLE documents DROP COLUMN IF EXISTS size_bytes;
