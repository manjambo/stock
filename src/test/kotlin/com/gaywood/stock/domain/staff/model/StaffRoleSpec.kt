package com.gaywood.stock.domain.staff.model

import com.gaywood.stock.domain.stock.model.StockLocation
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class StaffRoleSpec : BehaviorSpec({

    Given("a Worker role for BAR location") {
        val worker = StaffRole.Worker(StockLocation.BAR)

        Then("it should have basic permissions") {
            worker.hasPermission(Permission.VIEW_STOCK) shouldBe true
            worker.hasPermission(Permission.ADD_STOCK) shouldBe true
            worker.hasPermission(Permission.REMOVE_STOCK) shouldBe true
        }

        Then("it should NOT have manager permissions") {
            worker.hasPermission(Permission.ADJUST_STOCK) shouldBe false
            worker.hasPermission(Permission.SET_THRESHOLDS) shouldBe false
            worker.hasPermission(Permission.VIEW_AUDIT_LOG) shouldBe false
            worker.hasPermission(Permission.MANAGE_STAFF) shouldBe false
        }

        Then("it should only access BAR location") {
            worker.canAccessLocation(StockLocation.BAR) shouldBe true
            worker.canAccessLocation(StockLocation.KITCHEN) shouldBe false
        }

        Then("it should have Worker name") {
            worker.name shouldBe "Worker"
        }
    }

    Given("a Worker role for KITCHEN location") {
        val worker = StaffRole.Worker(StockLocation.KITCHEN)

        Then("it should only access KITCHEN location") {
            worker.canAccessLocation(StockLocation.KITCHEN) shouldBe true
            worker.canAccessLocation(StockLocation.BAR) shouldBe false
        }
    }

    Given("a Manager role with all locations") {
        val manager = StaffRole.Manager()

        Then("it should have all permissions") {
            Permission.entries.forEach { permission ->
                manager.hasPermission(permission) shouldBe true
            }
        }

        Then("it should access all locations") {
            StockLocation.entries.forEach { location ->
                manager.canAccessLocation(location) shouldBe true
            }
        }

        Then("it should have Manager name") {
            manager.name shouldBe "Manager"
        }
    }

    Given("a Manager role with only BAR location") {
        val barManager = StaffRole.Manager(setOf(StockLocation.BAR))

        Then("it should have all permissions") {
            Permission.entries.forEach { permission ->
                barManager.hasPermission(permission) shouldBe true
            }
        }

        Then("it should only access BAR location") {
            barManager.canAccessLocation(StockLocation.BAR) shouldBe true
            barManager.canAccessLocation(StockLocation.KITCHEN) shouldBe false
        }
    }

    Given("a Manager role with only KITCHEN location") {
        val kitchenManager = StaffRole.Manager(setOf(StockLocation.KITCHEN))

        Then("it should only access KITCHEN location") {
            kitchenManager.canAccessLocation(StockLocation.KITCHEN) shouldBe true
            kitchenManager.canAccessLocation(StockLocation.BAR) shouldBe false
        }
    }

    Given("comparing permissions between Worker and Manager") {
        val worker = StaffRole.Worker(StockLocation.BAR)
        val manager = StaffRole.Manager()

        val workerOnlyPermissions = setOf(
            Permission.VIEW_STOCK,
            Permission.ADD_STOCK,
            Permission.REMOVE_STOCK
        )

        val managerOnlyPermissions = setOf(
            Permission.ADJUST_STOCK,
            Permission.SET_THRESHOLDS,
            Permission.VIEW_AUDIT_LOG,
            Permission.MANAGE_STAFF
        )

        Then("worker should have basic permissions") {
            workerOnlyPermissions.forEach { permission ->
                worker.hasPermission(permission) shouldBe true
            }
        }

        Then("worker should not have manager-only permissions") {
            managerOnlyPermissions.forEach { permission ->
                worker.hasPermission(permission) shouldBe false
            }
        }

        Then("manager should have all permissions") {
            (workerOnlyPermissions + managerOnlyPermissions).forEach { permission ->
                manager.hasPermission(permission) shouldBe true
            }
        }
    }
})
