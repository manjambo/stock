package com.gaywood.stock.domain.staff.model

import com.gaywood.stock.domain.shared.AggregateRoot
import com.gaywood.stock.domain.stock.model.StockLocation

class Staff private constructor(
    override val id: StaffId,
    val name: StaffName,
    private var _role: StaffRole
) : AggregateRoot<StaffId>() {

    val role: StaffRole
        get() = _role

    fun hasPermission(permission: Permission): Boolean = _role.hasPermission(permission)

    fun canAccessLocation(location: StockLocation): Boolean = _role.canAccessLocation(location)

    fun changeRole(newRole: StaffRole) {
        _role = newRole
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
