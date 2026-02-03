package com.gaywood.stock.domain.staff.model

import com.gaywood.stock.domain.shared.AggregateRoot
import com.gaywood.stock.domain.staff.event.StaffEvent
import com.gaywood.stock.domain.stock.model.StockLocation
import java.time.Instant

class Staff private constructor(
    override val id: StaffId,
    val name: StaffName,
    private var _role: StaffRole
) : AggregateRoot<StaffId>() {

    val role: StaffRole
        get() = _role

    fun hasPermission(permission: Permission): Boolean = _role.hasPermission(permission)

    fun canAccessLocation(location: StockLocation): Boolean = _role.canAccessLocation(location)

    /**
     * Promotes a worker to manager with access to the specified locations.
     * Raises a [StaffEvent.StaffRoleChanged] domain event.
     */
    fun promoteToManager(locations: Set<StockLocation> = StockLocation.entries.toSet()) {
        val previousRole = _role
        _role = StaffRole.Manager(locations)
        registerEvent(StaffEvent.StaffRoleChanged(id, previousRole, _role, Instant.now()))
    }

    /**
     * Demotes a manager to worker with access to only the specified location.
     * Raises a [StaffEvent.StaffRoleChanged] domain event.
     */
    fun demoteToWorker(location: StockLocation) {
        val previousRole = _role
        _role = StaffRole.Worker(location)
        registerEvent(StaffEvent.StaffRoleChanged(id, previousRole, _role, Instant.now()))
    }

    /**
     * Changes the staff member's role.
     * Raises a [StaffEvent.StaffRoleChanged] domain event.
     */
    fun changeRole(newRole: StaffRole) {
        val previousRole = _role
        _role = newRole
        registerEvent(StaffEvent.StaffRoleChanged(id, previousRole, _role, Instant.now()))
    }

    companion object {
        fun create(
            id: StaffId = StaffId.generate(),
            name: StaffName,
            role: StaffRole
        ): Staff {
            return Staff(
                id = id,
                name = name,
                _role = role
            )
        }
    }
}
