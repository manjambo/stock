package com.gaywood.stock.domain.order.model

import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.domain.menu.model.Price
import com.gaywood.stock.domain.order.event.OrderCreated
import com.gaywood.stock.domain.order.event.OrderStatusChanged
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.fixtures.OrderFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal

class OrderSpec : BehaviorSpec({

    Given("creating a new order") {
        val staffId = StaffId.generate()
        val tableNumber = 5

        When("with valid items") {
            val ginItem = OrderFixtures.ginOrderItem(quantity = 2)
            val tonicItem = OrderFixtures.tonicOrderItem(quantity = 2)

            val order = Order.create(
                items = listOf(ginItem, tonicItem),
                tableNumber = tableNumber,
                staffId = staffId
            )

            Then("the order is created with pending status") {
                order.status shouldBe OrderStatus.PENDING
                order.tableNumber shouldBe tableNumber
                order.staffId shouldBe staffId
                order.items shouldHaveSize 2
            }

            Then("an OrderCreated event is registered") {
                order.domainEvents shouldHaveSize 1
                order.domainEvents.first().shouldBeInstanceOf<OrderCreated>()
            }
        }

        When("with no table number") {
            val order = Order.create(
                items = listOf(OrderFixtures.ginOrderItem()),
                tableNumber = null,
                staffId = staffId
            )

            Then("the order is created without a table number") {
                order.tableNumber shouldBe null
            }
        }
    }

    Given("calculating order total") {
        val staffId = StaffId.generate()

        When("order has multiple items") {
            // 2x Gin at £3.50 = £7.00
            // 2x Tonic at £1.00 = £2.00
            // 2x Fish & Chips at £8.50 = £17.00
            // Total = £26.00
            val items = listOf(
                OrderFixtures.ginOrderItem(quantity = 2),
                OrderFixtures.tonicOrderItem(quantity = 2),
                OrderFixtures.fishAndChipsOrderItem(quantity = 2)
            )

            val order = Order.create(items = items, staffId = staffId)

            Then("total is calculated correctly") {
                order.totalAmount shouldBe Price(BigDecimal("26.00"))
            }
        }

        When("order has no items") {
            val order = Order.create(items = emptyList(), staffId = staffId)

            Then("total is zero") {
                order.totalAmount shouldBe Price.zero()
            }
        }
    }

    Given("an existing pending order for status update") {
        val order = OrderFixtures.pendingOrder(
            items = listOf(OrderFixtures.ginOrderItem())
        )

        When("updating status to IN_PROGRESS") {
            order.clearEvents()
            order.updateStatus(OrderStatus.IN_PROGRESS)

            Then("status is updated") {
                order.status shouldBe OrderStatus.IN_PROGRESS
            }

            Then("a status changed event is registered") {
                order.domainEvents shouldHaveSize 1
                val event = order.domainEvents.first() as OrderStatusChanged
                event.previousStatus shouldBe OrderStatus.PENDING
                event.newStatus shouldBe OrderStatus.IN_PROGRESS
            }
        }
    }

    Given("a pending order for adding items") {
        val order = OrderFixtures.pendingOrder(
            items = listOf(OrderFixtures.ginOrderItem())
        )

        When("adding an item") {
            val newItem = OrderFixtures.tonicOrderItem()
            order.addItem(newItem)

            Then("item is added to the order") {
                order.items shouldHaveSize 2
            }
        }
    }

    Given("an in-progress order") {
        val order = OrderFixtures.pendingOrder(
            items = listOf(OrderFixtures.ginOrderItem())
        )
        order.updateStatus(OrderStatus.IN_PROGRESS)
        order.clearEvents()

        When("trying to add an item") {
            Then("it throws an exception") {
                shouldThrow<IllegalArgumentException> {
                    order.addItem(OrderFixtures.tonicOrderItem())
                }
            }
        }

        When("updating to READY status") {
            order.updateStatus(OrderStatus.READY)

            Then("status is updated") {
                order.status shouldBe OrderStatus.READY
            }
        }
    }

    Given("order status transitions") {
        When("attempting invalid transition from PENDING to READY") {
            val order = OrderFixtures.pendingOrder()

            Then("it throws an exception") {
                shouldThrow<IllegalArgumentException> {
                    order.updateStatus(OrderStatus.READY)
                }
            }
        }

        When("attempting invalid transition from PAID to any status") {
            val order = OrderFixtures.pendingOrder()
            order.updateStatus(OrderStatus.IN_PROGRESS)
            order.updateStatus(OrderStatus.READY)
            order.updateStatus(OrderStatus.SERVED)
            order.updateStatus(OrderStatus.PAID)

            Then("no transitions are valid") {
                shouldThrow<IllegalArgumentException> {
                    order.updateStatus(OrderStatus.CANCELLED)
                }
            }
        }
    }

    Given("cancelling an order") {
        When("order is pending") {
            val order = OrderFixtures.pendingOrder()
            order.clearEvents()
            order.cancel()

            Then("order is cancelled") {
                order.status shouldBe OrderStatus.CANCELLED
            }

            Then("a status changed event is registered") {
                order.domainEvents shouldHaveSize 1
            }
        }

        When("order is already paid") {
            val order = OrderFixtures.pendingOrder()
            order.updateStatus(OrderStatus.IN_PROGRESS)
            order.updateStatus(OrderStatus.READY)
            order.updateStatus(OrderStatus.SERVED)
            order.updateStatus(OrderStatus.PAID)

            Then("cancellation throws an exception") {
                shouldThrow<IllegalArgumentException> {
                    order.cancel()
                }
            }
        }
    }

    Given("generating a bill") {
        val items = listOf(
            OrderFixtures.ginOrderItem(quantity = 2),
            OrderFixtures.fishAndChipsOrderItem(quantity = 2)
        )
        val order = OrderFixtures.pendingOrder(tableNumber = 5, items = items)

        When("bill is generated") {
            val bill = order.generateBill()

            Then("bill contains correct information") {
                bill.orderId shouldBe order.id
                bill.tableNumber shouldBe 5
                bill.items shouldHaveSize 2
                bill.totalAmount shouldBe Price(BigDecimal("24.00"))
            }

            Then("bill can be formatted as text") {
                val text = bill.formatAsText()
                text.contains("ITEMISED BILL") shouldBe true
                text.contains("Table: 5") shouldBe true
                text.contains("Gin") shouldBe true
                text.contains("Fish and Chips") shouldBe true
            }
        }
    }
})
