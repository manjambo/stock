package com.gaywood.stock.domain.order.model

enum class OrderStatus(val displayName: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    READY("Ready"),
    SERVED("Served"),
    PAID("Paid"),
    CANCELLED("Cancelled")
}
