package com.gaywood.stock.domain.staff.model

import com.gaywood.stock.domain.stock.model.StockLocation

sealed class StaffRole(
    val name: String,
    val permissions: Set<Permission>,
    val allowedLocations: Set<StockLocation>
) {

    fun hasPermission(permission: Permission): Boolean = permission in permissions

    fun canAccessLocation(location: StockLocation): Boolean = location in allowedLocations

    data class Worker(
        val location: StockLocation
    ) : StaffRole(
        name = "Worker",
        permissions = setOf(
            Permission.VIEW_STOCK,
            Permission.ADD_STOCK,
            Permission.REMOVE_STOCK
        ),
        allowedLocations = setOf(location)
    )

    data class Manager(
        val locations: Set<StockLocation> = StockLocation.entries.toSet()
    ) : StaffRole(
        name = "Manager",
        permissions = setOf(
            Permission.VIEW_STOCK,
            Permission.ADD_STOCK,
            Permission.REMOVE_STOCK,
            Permission.ADJUST_STOCK,
            Permission.SET_THRESHOLDS,
            Permission.VIEW_AUDIT_LOG,
            Permission.MANAGE_STAFF
        ),
        allowedLocations = locations
    )

    override fun toString(): String = name
}
