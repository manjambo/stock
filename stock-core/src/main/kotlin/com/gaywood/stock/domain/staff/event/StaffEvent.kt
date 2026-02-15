package com.gaywood.stock.domain.staff.event

import com.gaywood.stock.domain.shared.DomainEvent
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.domain.staff.model.StaffRole
import java.time.Instant

/**
 * Domain events related to Staff aggregate changes.
 */
sealed class StaffEvent : DomainEvent {
    abstract val staffId: StaffId

    /**
     * Raised when a staff member's role changes (promotion, demotion, or reassignment).
     */
    data class StaffRoleChanged(
        override val staffId: StaffId,
        val previousRole: StaffRole,
        val newRole: StaffRole,
        override val occurredAt: Instant
    ) : StaffEvent()
}
