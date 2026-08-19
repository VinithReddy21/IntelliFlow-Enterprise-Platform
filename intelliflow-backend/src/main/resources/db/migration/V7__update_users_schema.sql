-- Flyway V7 Migration: Align users table schema with UserEntity domain aggregate root

-- 1. Add missing columns to users table
ALTER TABLE users
ADD COLUMN IF NOT EXISTS first_name VARCHAR(100) NOT NULL,
ADD COLUMN IF NOT EXISTS last_name VARCHAR(100) NOT NULL,
ADD COLUMN IF NOT EXISTS department_id UUID,
ADD COLUMN IF NOT EXISTS email_verified_at TIMESTAMP WITH TIME ZONE;

-- 2. Align failed_login_attempts nullability and default value
ALTER TABLE users
ALTER COLUMN failed_login_attempts SET DEFAULT 0,
ALTER COLUMN failed_login_attempts SET NOT NULL;
