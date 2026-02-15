-- Add cached allergens table for menu items
-- This stores pre-computed allergens from stock item ingredients for efficient querying

CREATE TABLE menu_item_cached_allergens (
    menu_item_id VARCHAR(36) NOT NULL,
    allergen VARCHAR(50) NOT NULL,
    PRIMARY KEY (menu_item_id, allergen),
    CONSTRAINT fk_cached_allergens_menu_item
        FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
);

CREATE INDEX idx_menu_item_cached_allergens_allergen ON menu_item_cached_allergens(allergen);
