-- V5: Create menu tables for bar and food menus

-- Main menu table
CREATE TABLE IF NOT EXISTS menus (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    menu_type VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_menu_type CHECK (menu_type IN ('BAR', 'FOOD'))
);

-- Menu items table
CREATE TABLE IF NOT EXISTS menu_items (
    id VARCHAR(36) PRIMARY KEY,
    menu_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price_amount DECIMAL(19, 4) NOT NULL,
    price_currency VARCHAR(3) NOT NULL DEFAULT 'GBP',
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_menu_items_menu
        FOREIGN KEY (menu_id)
        REFERENCES menus(id)
        ON DELETE CASCADE
);

-- Menu item ingredients (links menu items to stock items)
CREATE TABLE IF NOT EXISTS menu_item_ingredients (
    id VARCHAR(36) PRIMARY KEY,
    menu_item_id VARCHAR(36) NOT NULL,
    stock_item_id VARCHAR(36) NOT NULL,
    quantity_amount DECIMAL(19, 4) NOT NULL,
    quantity_unit VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_menu_item_ingredients_menu_item
        FOREIGN KEY (menu_item_id)
        REFERENCES menu_items(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_menu_item_ingredients_stock_item
        FOREIGN KEY (stock_item_id)
        REFERENCES stock_items(id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_menu_item_stock_item
        UNIQUE (menu_item_id, stock_item_id)
);

-- Indexes for efficient querying
CREATE INDEX IF NOT EXISTS idx_menus_type ON menus(menu_type);
CREATE INDEX IF NOT EXISTS idx_menus_active ON menus(active);
CREATE INDEX IF NOT EXISTS idx_menu_items_menu_id ON menu_items(menu_id);
CREATE INDEX IF NOT EXISTS idx_menu_item_ingredients_menu_item_id ON menu_item_ingredients(menu_item_id);
CREATE INDEX IF NOT EXISTS idx_menu_item_ingredients_stock_item_id ON menu_item_ingredients(stock_item_id);
