-- Flyway V6 Migration: Align tasks table schema with TaskEntity domain aggregate root

ALTER TABLE tasks
ADD COLUMN IF NOT EXISTS due_date TIMESTAMP WITH TIME ZONE,
ADD COLUMN IF NOT EXISTS started_at TIMESTAMP WITH TIME ZONE,
ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP WITH TIME ZONE,
ADD COLUMN IF NOT EXISTS estimated_hours NUMERIC(5, 2),
ADD COLUMN IF NOT EXISTS actual_hours NUMERIC(5, 2),
ADD COLUMN IF NOT EXISTS is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS recurrence_cron VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by UUID,
ADD COLUMN IF NOT EXISTS updated_by UUID;

-- Query performance indexes supporting TaskRepository query methods
CREATE INDEX IF NOT EXISTS idx_tasks_creator_id ON tasks(creator_id);
CREATE INDEX IF NOT EXISTS idx_tasks_department_id ON tasks(department_id);
CREATE INDEX IF NOT EXISTS idx_tasks_parent_task_id ON tasks(parent_task_id);
