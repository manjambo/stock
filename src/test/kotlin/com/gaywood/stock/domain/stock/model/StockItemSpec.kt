package com.gaywood.stock.domain.stock.model

import com.gaywood.stock.domain.shared.InsufficientStockException
import com.gaywood.stock.domain.stock.event.StockEvent
import com.gaywood.stock.fixtures.StockFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class StockItemSpec : BehaviorSpec({

    Given("a stock item with initial quantity") {
        val vodka = StockFixtures.vodkaBottle(quantity = 10)

        Then("it should have the correct initial state") {
            vodka.name shouldBe "Absolut Vodka"
            vodka.category shouldBe StockCategory.Bar.Spirits
            vodka.location shouldBe StockLocation.BAR
            vodka.quantity.amount.toInt() shouldBe 10
            vodka.quantity.unit shouldBe Unit.BOTTLES
        }
    }

    Given("a stock item") {
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = null)

        When("adding stock") {
            vodka.addStock(Quantity(5, Unit.BOTTLES))

            Then("quantity should increase") {
                vodka.quantity.amount.toInt() shouldBe 15
            }

            Then("a StockAdded event should be raised") {
                vodka.domainEvents shouldHaveSize 1
                val event = vodka.domainEvents.first()
                event.shouldBeInstanceOf<StockEvent.StockAdded>()
                (event as StockEvent.StockAdded).quantityAdded.amount.toInt() shouldBe 5
                event.newTotal.amount.toInt() shouldBe 15
            }
        }
    }

    Given("a stock item with sufficient quantity") {
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = null)

        When("removing stock within available quantity") {
            vodka.removeStock(Quantity(3, Unit.BOTTLES))

            Then("quantity should decrease") {
                vodka.quantity.amount.toInt() shouldBe 7
            }

            Then("a StockRemoved event should be raised") {
                vodka.domainEvents shouldHaveSize 1
                val event = vodka.domainEvents.first()
                event.shouldBeInstanceOf<StockEvent.StockRemoved>()
                (event as StockEvent.StockRemoved).quantityRemoved.amount.toInt() shouldBe 3
                event.newTotal.amount.toInt() shouldBe 7
            }
        }
    }

    Given("a stock item with insufficient quantity") {
        val vodka = StockFixtures.vodkaBottle(quantity = 5, lowStockThreshold = null)

        When("trying to remove more than available") {
            Then("it should throw InsufficientStockException") {
                val exception = shouldThrow<InsufficientStockException> {
                    vodka.removeStock(Quantity(10, Unit.BOTTLES))
                }
                exception.message shouldBe "Insufficient stock for 'Absolut Vodka': requested 10.00 bottles but only 5.00 bottles available"
            }
        }
    }

    Given("a stock item with low stock threshold") {
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = 5)

        When("removing stock below threshold") {
            vodka.removeStock(Quantity(6, Unit.BOTTLES))

            Then("quantity should be below threshold") {
                vodka.quantity.amount.toInt() shouldBe 4
                vodka.isLowStock() shouldBe true
            }

            Then("a LowStockAlertRaised event should be raised") {
                val alertEvents = vodka.domainEvents.filterIsInstance<StockEvent.LowStockAlertRaised>()
                alertEvents shouldHaveSize 1
                alertEvents.first().currentQuantity.amount.toInt() shouldBe 4
                alertEvents.first().threshold.amount.toInt() shouldBe 5
            }
        }
    }

    Given("a stock item at exactly the threshold") {
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = 5)

        When("removing stock to exactly the threshold") {
            vodka.removeStock(Quantity(5, Unit.BOTTLES))

            Then("it should be considered low stock") {
                vodka.quantity.amount.toInt() shouldBe 5
                vodka.isLowStock() shouldBe true
            }
        }
    }

    Given("a stock item without threshold") {
        val beer = StockFixtures.craftBeer(quantity = 48)

        Then("isLowStock should return false") {
            beer.isLowStock() shouldBe false
        }

        When("setting a threshold") {
            beer.setLowStockThreshold(LowStockThreshold(Quantity(10, Unit.BOTTLES)))

            Then("a ThresholdUpdated event should be raised") {
                val thresholdEvents = beer.domainEvents.filterIsInstance<StockEvent.ThresholdUpdated>()
                thresholdEvents shouldHaveSize 1
                thresholdEvents.first().previousThreshold shouldBe null
                thresholdEvents.first().newThreshold.amount.toInt() shouldBe 10
            }
        }
    }

    Given("a stock item for adjustment") {
        val chicken = StockFixtures.chickenBreast(quantity = 10.0, lowStockThreshold = null)

        When("adjusting the stock quantity") {
            chicken.adjustStock(Quantity(15.0, Unit.KILOGRAMS), "Inventory recount")

            Then("quantity should be updated to new value") {
                chicken.quantity.amount.toDouble() shouldBe 15.0
            }

            Then("a StockAdjusted event should be raised") {
                val adjustedEvents = chicken.domainEvents.filterIsInstance<StockEvent.StockAdjusted>()
                adjustedEvents shouldHaveSize 1
                adjustedEvents.first().previousQuantity.amount.toDouble() shouldBe 10.0
                adjustedEvents.first().newQuantity.amount.toDouble() shouldBe 15.0
                adjustedEvents.first().reason shouldBe "Inventory recount"
            }
        }
    }

    Given("a kitchen stock item") {
        val tomatoes = StockFixtures.tomatoes(quantity = 5.0)

        Then("it should have KITCHEN location") {
            tomatoes.location shouldBe StockLocation.KITCHEN
            tomatoes.category shouldBe StockCategory.Kitchen.Vegetables
        }
    }

    Given("clearing domain events") {
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = null)
        vodka.addStock(Quantity(5, Unit.BOTTLES))

        When("clearing events") {
            vodka.clearEvents()

            Then("events list should be empty") {
                vodka.domainEvents shouldHaveSize 0
            }
        }
    }

    Given("stock item creation validation") {
        Then("blank name should throw exception") {
            shouldThrow<IllegalArgumentException> {
                StockItem.create(
                    name = "",
                    category = StockCategory.Bar.Spirits,
                    initialQuantity = Quantity(10, Unit.BOTTLES)
                )
            }
        }

        Then("mismatched threshold unit should throw exception") {
            shouldThrow<IllegalArgumentException> {
                StockItem.create(
                    name = "Test",
                    category = StockCategory.Bar.Spirits,
                    initialQuantity = Quantity(10, Unit.BOTTLES),
                    lowStockThreshold = LowStockThreshold(Quantity(5, Unit.KILOGRAMS))
                )
            }
        }
    }
})
