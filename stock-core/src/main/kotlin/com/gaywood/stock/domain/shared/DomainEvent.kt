package com.gaywood.stock.domain.shared

import java.time.Instant

interface DomainEvent {
    val occurredAt: Instant
}
