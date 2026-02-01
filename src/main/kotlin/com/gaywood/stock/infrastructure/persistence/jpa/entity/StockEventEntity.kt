package com.gaywood.stock.infrastructure.persistence.jpa.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "stock_events")
class StockEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "stock_item_id", length = 36, nullable = false)
    var stockItemId: String = "",

    @Column(name = "event_type", length = 50, nullable = false)
    var eventType: String = "",

    @Column(name = "event_data", columnDefinition = "TEXT", nullable = false)
    var eventData: String = "",

    @Column(name = "occurred_at", nullable = false)
    var occurredAt: Instant = Instant.now(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now()
)
