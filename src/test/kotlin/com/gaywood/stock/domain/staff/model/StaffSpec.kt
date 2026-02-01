package com.gaywood.stock.domain.staff.model

import com.gaywood.stock.domain.stock.model.StockLocation
import com.gaywood.stock.fixtures.StaffFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class StaffSpec : BehaviorSpec({

    Given("a bar worker") {
        val barWorker = StaffFixtures.barWorker(firstName = "John", lastName = "Doe")

        Then("it should have correct name") {
            barWorker.name.firstName shouldBe "John"
            barWorker.name.lastName shouldBe "Doe"
            barWorker.name.fullName shouldBe "John Doe"
        }

        Then("it should have Worker role") {
            barWorker.role shouldBe StaffRole.Worker(StockLocation.BAR)
        }

        Then("it should have basic permissions") {
            barWorker.hasPermission(Permission.VIEW_STOCK) shouldBe true
            barWorker.hasPermission(Permission.ADD_STOCK) shouldBe true
            barWorker.hasPermission(Permission.REMOVE_STOCK) shouldBe true
        }

        Then("it should NOT have manager permissions") {
            barWorker.hasPermission(Permission.ADJUST_STOCK) shouldBe false
            barWorker.hasPermission(Permission.SET_THRESHOLDS) shouldBe false
            barWorker.hasPermission(Permission.VIEW_AUDIT_LOG) shouldBe false
            barWorker.hasPermission(Permission.MANAGE_STAFF) shouldBe false
        }

        Then("it should only access BAR location") {
            barWorker.canAccessLocation(StockLocation.BAR) shouldBe true
            barWorker.canAccessLocation(StockLocation.KITCHEN) shouldBe false
        }
    }

    Given("a kitchen worker") {
        val kitchenWorker = StaffFixtures.kitchenWorker()

        Then("it should only access KITCHEN location") {
            kitchenWorker.canAccessLocation(StockLocation.KITCHEN) shouldBe true
            kitchenWorker.canAccessLocation(StockLocation.BAR) shouldBe false
        }
    }

    Given("a manager with all locations") {
        val manager = StaffFixtures.manager()

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
    }

    Given("a bar manager") {
        val barManager = StaffFixtures.barManager()

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

    Given("a staff member changing roles") {
        val staff = StaffFixtures.barWorker()

        When("promoting to manager") {
            staff.changeRole(StaffRole.Manager())

            Then("they should have manager permissions") {
                staff.hasPermission(Permission.ADJUST_STOCK) shouldBe true
                staff.hasPermission(Permission.SET_THRESHOLDS) shouldBe true
                staff.hasPermission(Permission.VIEW_AUDIT_LOG) shouldBe true
                staff.hasPermission(Permission.MANAGE_STAFF) shouldBe true
            }

            Then("they should access all locations") {
                StockLocation.entries.forEach { location ->
                    staff.canAccessLocation(location) shouldBe true
                }
            }
        }
    }

    Given("a manager being demoted to worker") {
        val staff = StaffFixtures.manager()

        When("demoting to kitchen worker") {
            staff.changeRole(StaffRole.Worker(StockLocation.KITCHEN))

            Then("they should lose manager permissions") {
                staff.hasPermission(Permission.ADJUST_STOCK) shouldBe false
                staff.hasPermission(Permission.SET_THRESHOLDS) shouldBe false
            }

            Then("they should only access KITCHEN") {
                staff.canAccessLocation(StockLocation.KITCHEN) shouldBe true
                staff.canAccessLocation(StockLocation.BAR) shouldBe false
            }
        }
    }

    Given("staff name validation") {
        Then("blank first name should throw exception") {
            shouldThrow<IllegalArgumentException> {
                StaffName("", "Doe")
            }
        }

        Then("blank last name should throw exception") {
            shouldThrow<IllegalArgumentException> {
                StaffName("John", "")
            }
        }
    }
})
