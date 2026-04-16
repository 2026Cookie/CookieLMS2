-- Migration: Add Severity to Error Logs
-- Purpose: Add severity column to error_logs table for error classification
-- Date: 2026-04-15

ALTER TABLE error_logs
ADD COLUMN severity VARCHAR(20)
COMMENT '에러 심각도 (INFO, WARNING, CRITICAL)';

-- Index for efficient severity lookups
CREATE INDEX idx_severity ON error_logs(severity);

-- Index for combined lookup (severity + created_at)
CREATE INDEX idx_severity_created_at ON error_logs(severity, created_at DESC);
