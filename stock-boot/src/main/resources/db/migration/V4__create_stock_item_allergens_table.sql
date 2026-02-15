-- V4: Create stock_item_allergens junction table for many-to-many relationship
-- A stock item can have zero or many allergens

CREATE TABLE IF NOT EXISTS stock_item_allergens (
    stock_item_id VARCHAR(36) NOT NULL,
    allergen VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (stock_item_id, allergen),

    CONSTRAINT fk_stock_item_allergens_stock_item
        FOREIGN KEY (stock_item_id)
        REFERENCES stock_items(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_allergen_valid CHECK (
        allergen IN (
            'CELERY',
            'GLUTEN',
            'CRUSTACEANS',
            'EGGS',
            'FISH',
            'LUPIN',
            'MILK',
            'MOLLUSCS',
            'MUSTARD',
            'TREE_NUTS',
            'PEANUTS',
            'SESAME',
            'SOYBEANS',
            'SULPHITES'
        )
    )
);

-- Index for efficient querying by allergen
CREATE INDEX IF NOT EXISTS idx_stock_item_allergens_allergen
    ON stock_item_allergens(allergen);
