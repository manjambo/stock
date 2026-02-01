-- V2: Create staff table
-- Idempotent: Uses IF NOT EXISTS

CREATE TABLE IF NOT EXISTS staff (
    id VARCHAR(36) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role_type VARCHAR(20) NOT NULL,
    role_locations VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_role_type CHECK (role_type IN ('WORKER', 'MANAGER'))
);

-- Create index for role-based queries
CREATE INDEX IF NOT EXISTS idx_staff_role_type ON staff(role_type);
