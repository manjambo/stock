package com.gaywood.stock.infrastructure.config;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

    @Label("Order Notifications - Send notifications when order status changes")
    ORDER_NOTIFICATIONS,

    @Label("Low Stock Alerts - Automatically alert when stock falls below threshold")
    @EnabledByDefault
    LOW_STOCK_ALERTS,

    @Label("Kitchen Display - Enable kitchen display system integration")
    KITCHEN_DISPLAY,

    @Label("Table Reservations - Enable table reservation functionality")
    TABLE_RESERVATIONS,

    @Label("Loyalty Points - Enable customer loyalty points system")
    LOYALTY_POINTS;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
