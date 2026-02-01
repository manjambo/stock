-- V1: Create stock_items table
-- Idempotent: Uses IF NOT EXISTS

CREATE TABLE IF NOT EXISTS stock_items (
    id VARCHAR(36) PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    location VARCHAR(20) NOT NULL,
    quantity_amount DECIMAL(19, 4) NOT NULL,
    quantity_unit VARCHAR(20) NOT NULL,
    low_stock_threshold_amount DECIMAL(19, 4),
    low_stock_threshold_unit VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_quantity_positive CHECK (quantity_amount >= 0),
    CONSTRAINT chk_threshold_positive CHECK (low_stock_threshold_amount IS NULL OR low_stock_threshold_amount >= 0),
    CONSTRAINT chk_threshold_unit_match CHECK (
        (low_stock_threshold_amount IS NULL AND low_stock_threshold_unit IS NULL) OR
        (low_stock_threshold_amount IS NOT NULL AND low_stock_threshold_unit IS NOT NULL)
    )
);

-- Create index for location-based queries
CREATE INDEX IF NOT EXISTS idx_stock_items_location ON stock_items(location);

-- Create index for category-based queries
CREATE INDEX IF NOT EXISTS idx_stock_items_category ON stock_items(category);
