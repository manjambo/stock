-- V6: Create order tables for order management

-- Main orders table
CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(36) PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    table_number INTEGER,
    staff_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'READY', 'SERVED', 'PAID', 'CANCELLED')),
    CONSTRAINT fk_orders_staff
        FOREIGN KEY (staff_id)
        REFERENCES staff(id)
        ON DELETE RESTRICT
);

-- Order items table
CREATE TABLE IF NOT EXISTS order_items (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    menu_item_id VARCHAR(36) NOT NULL,
    menu_item_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price_amount DECIMAL(19, 4) NOT NULL,
    unit_price_currency VARCHAR(3) NOT NULL DEFAULT 'GBP',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_order_items_menu_item
        FOREIGN KEY (menu_item_id)
        REFERENCES menu_items(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_quantity_positive CHECK (quantity > 0)
);

-- Indexes for efficient querying
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_staff_id ON orders(staff_id);
CREATE INDEX IF NOT EXISTS idx_orders_table_number ON orders(table_number);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
