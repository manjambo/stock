package com.gaywood.stock.domain.staff.model

import com.gaywood.stock.domain.staff.event.StaffEvent
import com.gaywood.stock.domain.stock.model.StockLocation
import com.gaywood.stock.fixtures.StaffFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

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

    Given("a worker being promoted to manager") {
        val worker = StaffFixtures.barWorker()
        worker.clearEvents()

        When("promoting to manager with all locations") {
            worker.promoteToManager()

            Then("they should have manager role") {
                worker.role.shouldBeInstanceOf<StaffRole.Manager>()
            }

            Then("they should have all permissions") {
                worker.hasPermission(Permission.ADJUST_STOCK) shouldBe true
                worker.hasPermission(Permission.MANAGE_STAFF) shouldBe true
            }

            Then("they should access all locations") {
                worker.canAccessLocation(StockLocation.BAR) shouldBe true
                worker.canAccessLocation(StockLocation.KITCHEN) shouldBe true
            }

            Then("a StaffRoleChanged event should be raised") {
                worker.domainEvents shouldHaveSize 1
                val event = worker.domainEvents.first()
                event.shouldBeInstanceOf<StaffEvent.StaffRoleChanged>()
                (event as StaffEvent.StaffRoleChanged).previousRole.shouldBeInstanceOf<StaffRole.Worker>()
                event.newRole.shouldBeInstanceOf<StaffRole.Manager>()
            }
        }
    }

    Given("a worker being promoted to manager with specific locations") {
        val worker = StaffFixtures.kitchenWorker()
        worker.clearEvents()

        When("promoting to manager with only BAR location") {
            worker.promoteToManager(setOf(StockLocation.BAR))

            Then("they should only access BAR") {
                worker.canAccessLocation(StockLocation.BAR) shouldBe true
                worker.canAccessLocation(StockLocation.KITCHEN) shouldBe false
            }
        }
    }

    Given("a manager being demoted to worker") {
        val manager = StaffFixtures.manager()
        manager.clearEvents()

        When("demoting to kitchen worker") {
            manager.demoteToWorker(StockLocation.KITCHEN)

            Then("they should have worker role") {
                manager.role.shouldBeInstanceOf<StaffRole.Worker>()
            }

            Then("they should lose manager permissions") {
                manager.hasPermission(Permission.ADJUST_STOCK) shouldBe false
                manager.hasPermission(Permission.MANAGE_STAFF) shouldBe false
            }

            Then("they should only access KITCHEN") {
                manager.canAccessLocation(StockLocation.KITCHEN) shouldBe true
                manager.canAccessLocation(StockLocation.BAR) shouldBe false
            }

            Then("a StaffRoleChanged event should be raised") {
                manager.domainEvents shouldHaveSize 1
                val event = manager.domainEvents.first()
                event.shouldBeInstanceOf<StaffEvent.StaffRoleChanged>()
                (event as StaffEvent.StaffRoleChanged).previousRole.shouldBeInstanceOf<StaffRole.Manager>()
                event.newRole.shouldBeInstanceOf<StaffRole.Worker>()
            }
        }
    }

    Given("a staff member changing roles via changeRole") {
        val staff = StaffFixtures.barWorker()
        staff.clearEvents()

        When("changing role") {
            staff.changeRole(StaffRole.Manager())

            Then("a StaffRoleChanged event should be raised") {
                staff.domainEvents shouldHaveSize 1
                staff.domainEvents.first().shouldBeInstanceOf<StaffEvent.StaffRoleChanged>()
            }
        }
    }
})
