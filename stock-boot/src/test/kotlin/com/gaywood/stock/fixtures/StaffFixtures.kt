package com.gaywood.stock.fixtures

import com.gaywood.stock.domain.staff.model.Staff
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.domain.staff.model.StaffName
import com.gaywood.stock.domain.staff.model.StaffRole
import com.gaywood.stock.domain.stock.model.StockLocation

object StaffFixtures {

    fun barWorker(
        id: StaffId = StaffId.generate(),
        firstName: String = "John",
        lastName: String = "Bartender"
    ): Staff = Staff.create(
        id = id,
        name = StaffName(firstName, lastName),
        role = StaffRole.Worker(StockLocation.BAR)
    )

    fun kitchenWorker(
        id: StaffId = StaffId.generate(),
        firstName: String = "Jane",
        lastName: String = "Chef"
    ): Staff = Staff.create(
        id = id,
        name = StaffName(firstName, lastName),
        role = StaffRole.Worker(StockLocation.KITCHEN)
    )

    fun manager(
        id: StaffId = StaffId.generate(),
        firstName: String = "Mike",
        lastName: String = "Manager",
        locations: Set<StockLocation> = StockLocation.entries.toSet()
    ): Staff = Staff.create(
        id = id,
        name = StaffName(firstName, lastName),
        role = StaffRole.Manager(locations)
    )

    fun barManager(
        id: StaffId = StaffId.generate(),
        firstName: String = "Sarah",
        lastName: String = "BarManager"
    ): Staff = Staff.create(
        id = id,
        name = StaffName(firstName, lastName),
        role = StaffRole.Manager(setOf(StockLocation.BAR))
    )

    fun kitchenManager(
        id: StaffId = StaffId.generate(),
        firstName: String = "Tom",
        lastName: String = "KitchenManager"
    ): Staff = Staff.create(
        id = id,
        name = StaffName(firstName, lastName),
        role = StaffRole.Manager(setOf(StockLocation.KITCHEN))
    )
}
