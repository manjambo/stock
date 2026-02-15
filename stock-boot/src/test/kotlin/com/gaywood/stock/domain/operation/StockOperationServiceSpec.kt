package com.gaywood.stock.domain.operation

import com.gaywood.stock.domain.operation.service.StockOperationService
import com.gaywood.stock.domain.shared.InsufficientStockException
import com.gaywood.stock.domain.shared.LocationAccessDeniedException
import com.gaywood.stock.domain.shared.PermissionDeniedException
import com.gaywood.stock.domain.shared.StockItemNotFoundException
import com.gaywood.stock.domain.stock.model.LowStockThreshold
import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockLocation
import com.gaywood.stock.domain.stock.model.Unit
import com.gaywood.stock.fixtures.StaffFixtures
import com.gaywood.stock.fixtures.StockFixtures
import com.gaywood.stock.infrastructure.persistence.InMemoryStockRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class StockOperationServiceSpec : BehaviorSpec({

    Given("a bar worker viewing bar stock") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val barWorker = StaffFixtures.barWorker()
        val vodka = StockFixtures.vodkaBottle()
        val beer = StockFixtures.craftBeer()
        stockRepository.save(vodka)
        stockRepository.save(beer)

        When("viewing BAR stock") {
            val result = service.viewStock(barWorker, StockLocation.BAR)

            Then("it should return bar items") {
                result shouldHaveSize 2
                result shouldContain vodka
                result shouldContain beer
            }
        }

        When("trying to view KITCHEN stock") {
            Then("it should throw LocationAccessDeniedException") {
                shouldThrow<LocationAccessDeniedException> {
                    service.viewStock(barWorker, StockLocation.KITCHEN)
                }
            }
        }
    }

    Given("a kitchen worker viewing stock") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val kitchenWorker = StaffFixtures.kitchenWorker()
        val chicken = StockFixtures.chickenBreast()
        val vodka = StockFixtures.vodkaBottle()
        stockRepository.save(chicken)
        stockRepository.save(vodka)

        When("viewing KITCHEN stock") {
            val result = service.viewStock(kitchenWorker, StockLocation.KITCHEN)

            Then("it should return kitchen items only") {
                result shouldHaveSize 1
                result shouldContain chicken
            }
        }

        When("viewing all accessible stock") {
            val result = service.viewAllStock(kitchenWorker)

            Then("it should return only kitchen items") {
                result shouldHaveSize 1
                result shouldContain chicken
            }
        }
    }

    Given("a manager viewing all stock") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val manager = StaffFixtures.manager()
        val vodka = StockFixtures.vodkaBottle()
        val chicken = StockFixtures.chickenBreast()
        stockRepository.save(vodka)
        stockRepository.save(chicken)

        When("viewing all stock") {
            val result = service.viewAllStock(manager)

            Then("it should return all items") {
                result shouldHaveSize 2
                result shouldContain vodka
                result shouldContain chicken
            }
        }

        When("viewing BAR stock") {
            val result = service.viewStock(manager, StockLocation.BAR)

            Then("it should return bar items") {
                result shouldHaveSize 1
                result shouldContain vodka
            }
        }

        When("viewing KITCHEN stock") {
            val result = service.viewStock(manager, StockLocation.KITCHEN)

            Then("it should return kitchen items") {
                result shouldHaveSize 1
                result shouldContain chicken
            }
        }
    }

    Given("a bar worker adding stock") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val barWorker = StaffFixtures.barWorker()
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = null)
        stockRepository.save(vodka)

        When("adding stock to bar item") {
            val result = service.addStock(barWorker, vodka.id, Quantity(5, Unit.BOTTLES))

            Then("stock should be increased") {
                result.quantity.amount.toInt() shouldBe 15
            }
        }
    }

    Given("a bar worker trying to add stock to kitchen item") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val barWorker = StaffFixtures.barWorker()
        val chicken = StockFixtures.chickenBreast()
        stockRepository.save(chicken)

        Then("it should throw LocationAccessDeniedException") {
            shouldThrow<LocationAccessDeniedException> {
                service.addStock(barWorker, chicken.id, Quantity(1.0, Unit.KILOGRAMS))
            }
        }
    }

    Given("a bar worker removing stock") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val barWorker = StaffFixtures.barWorker()
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = null)
        stockRepository.save(vodka)

        When("removing available stock") {
            val result = service.removeStock(barWorker, vodka.id, Quantity(3, Unit.BOTTLES))

            Then("stock should be decreased") {
                result.quantity.amount.toInt() shouldBe 7
            }
        }
    }

    Given("a bar worker removing more stock than available") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val barWorker = StaffFixtures.barWorker()
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = null)
        stockRepository.save(vodka)

        Then("it should throw InsufficientStockException") {
            shouldThrow<InsufficientStockException> {
                service.removeStock(barWorker, vodka.id, Quantity(20, Unit.BOTTLES))
            }
        }
    }

    Given("a worker trying to adjust stock") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val barWorker = StaffFixtures.barWorker()
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = null)
        stockRepository.save(vodka)

        Then("it should throw PermissionDeniedException") {
            shouldThrow<PermissionDeniedException> {
                service.adjustStock(barWorker, vodka.id, Quantity(20, Unit.BOTTLES), "Recount")
            }.message shouldBe "Permission 'ADJUST_STOCK' denied for role 'Worker'"
        }
    }

    Given("a manager adjusting stock") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val manager = StaffFixtures.manager()
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = null)
        stockRepository.save(vodka)

        When("adjusting stock quantity") {
            val result = service.adjustStock(
                manager,
                vodka.id,
                Quantity(25, Unit.BOTTLES),
                "Inventory recount"
            )

            Then("stock should be adjusted") {
                result.quantity.amount.toInt() shouldBe 25
            }
        }
    }

    Given("a worker trying to set threshold") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val barWorker = StaffFixtures.barWorker()
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = null)
        stockRepository.save(vodka)

        Then("it should throw PermissionDeniedException") {
            shouldThrow<PermissionDeniedException> {
                service.setLowStockThreshold(
                    barWorker,
                    vodka.id,
                    LowStockThreshold(Quantity(5, Unit.BOTTLES))
                )
            }.message shouldBe "Permission 'SET_THRESHOLDS' denied for role 'Worker'"
        }
    }

    Given("a manager setting threshold") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val manager = StaffFixtures.manager()
        val vodka = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = null)
        stockRepository.save(vodka)

        When("setting threshold") {
            val result = service.setLowStockThreshold(
                manager,
                vodka.id,
                LowStockThreshold(Quantity(5, Unit.BOTTLES))
            )

            Then("threshold should be set") {
                result.lowStockThreshold?.quantity?.amount?.toInt() shouldBe 5
            }
        }
    }

    Given("viewing low stock items") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val manager = StaffFixtures.manager()
        val vodkaLow = StockFixtures.vodkaBottle(quantity = 3, lowStockThreshold = 5)
        val vodkaOk = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = 5)
        val chickenLow = StockFixtures.chickenBreast(quantity = 1.0, lowStockThreshold = 2.0)
        stockRepository.save(vodkaLow)
        stockRepository.save(vodkaOk)
        stockRepository.save(chickenLow)

        When("manager views low stock") {
            val result = service.viewLowStockItems(manager)

            Then("it should return only low stock items") {
                result shouldHaveSize 2
                result shouldContain vodkaLow
                result shouldContain chickenLow
            }
        }
    }

    Given("bar worker viewing low stock items") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val barWorker = StaffFixtures.barWorker()
        val vodkaLow = StockFixtures.vodkaBottle(quantity = 3, lowStockThreshold = 5)
        val chickenLow = StockFixtures.chickenBreast(quantity = 1.0, lowStockThreshold = 2.0)
        stockRepository.save(vodkaLow)
        stockRepository.save(chickenLow)

        When("bar worker views low stock") {
            val result = service.viewLowStockItems(barWorker)

            Then("it should return only accessible low stock items") {
                result shouldHaveSize 1
                result shouldContain vodkaLow
            }
        }
    }

    Given("operations on non-existent stock") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val manager = StaffFixtures.manager()
        val nonExistentId = com.gaywood.stock.domain.stock.model.StockItemId.generate()

        Then("addStock should throw StockItemNotFoundException") {
            shouldThrow<StockItemNotFoundException> {
                service.addStock(manager, nonExistentId, Quantity(5, Unit.BOTTLES))
            }
        }

        Then("removeStock should throw StockItemNotFoundException") {
            shouldThrow<StockItemNotFoundException> {
                service.removeStock(manager, nonExistentId, Quantity(5, Unit.BOTTLES))
            }
        }

        Then("adjustStock should throw StockItemNotFoundException") {
            shouldThrow<StockItemNotFoundException> {
                service.adjustStock(manager, nonExistentId, Quantity(5, Unit.BOTTLES), "Reason")
            }
        }
    }

    Given("a bar manager trying to access kitchen stock") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val barManager = StaffFixtures.barManager()
        val chicken = StockFixtures.chickenBreast()
        stockRepository.save(chicken)

        Then("it should throw LocationAccessDeniedException when adding") {
            shouldThrow<LocationAccessDeniedException> {
                service.addStock(barManager, chicken.id, Quantity(1.0, Unit.KILOGRAMS))
            }
        }

        Then("it should throw LocationAccessDeniedException when adjusting") {
            shouldThrow<LocationAccessDeniedException> {
                service.adjustStock(barManager, chicken.id, Quantity(5.0, Unit.KILOGRAMS), "Recount")
            }
        }
    }

    Given("empty stock repository") {
        val stockRepository = InMemoryStockRepository()
        val service = StockOperationService(stockRepository)
        val manager = StaffFixtures.manager()

        When("viewing all stock") {
            val result = service.viewAllStock(manager)

            Then("it should return empty list") {
                result.shouldBeEmpty()
            }
        }

        When("viewing low stock items") {
            val result = service.viewLowStockItems(manager)

            Then("it should return empty list") {
                result.shouldBeEmpty()
            }
        }
    }
})
