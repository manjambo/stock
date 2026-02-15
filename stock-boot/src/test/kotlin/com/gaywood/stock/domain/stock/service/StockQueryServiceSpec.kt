package com.gaywood.stock.domain.stock.service

import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.fixtures.StockFixtures
import com.gaywood.stock.infrastructure.persistence.InMemoryStockRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain

class StockQueryServiceSpec : BehaviorSpec({

    Given("a StockQueryService with stock items") {
        val repository = InMemoryStockRepository()
        val service = StockQueryService(repository)

        val vodkaLow = StockFixtures.vodkaBottle(quantity = 3, lowStockThreshold = 5)
        val vodkaOk = StockFixtures.vodkaBottle(quantity = 10, lowStockThreshold = 5)
        val chickenLow = StockFixtures.chickenBreast(quantity = 1.0, lowStockThreshold = 2.0)
        val chickenOk = StockFixtures.chickenBreast(quantity = 5.0, lowStockThreshold = 2.0)

        repository.save(vodkaLow)
        repository.save(vodkaOk)
        repository.save(chickenLow)
        repository.save(chickenOk)

        When("finding low stock items") {
            val result = service.findLowStockItems()

            Then("it should return only items below threshold") {
                result shouldHaveSize 2
                result shouldContain vodkaLow
                result shouldContain chickenLow
                result shouldNotContain vodkaOk
                result shouldNotContain chickenOk
            }
        }
    }

    Given("a StockQueryService with items containing allergens") {
        val repository = InMemoryStockRepository()
        val service = StockQueryService(repository)

        val milkItem = StockFixtures.chickenBreast().also {
            it.addAllergen(Allergen.MILK)
        }
        val glutenItem = StockFixtures.vodkaBottle().also {
            it.addAllergen(Allergen.GLUTEN)
        }
        val multiAllergenItem = StockFixtures.craftBeer().also {
            it.addAllergen(Allergen.GLUTEN)
            it.addAllergen(Allergen.SULPHITES)
        }
        val noAllergenItem = StockFixtures.vodkaBottle()

        repository.save(milkItem)
        repository.save(glutenItem)
        repository.save(multiAllergenItem)
        repository.save(noAllergenItem)

        When("finding items with a specific allergen") {
            val result = service.findItemsWithAllergen(Allergen.GLUTEN)

            Then("it should return items containing that allergen") {
                result shouldHaveSize 2
                result shouldContain glutenItem
                result shouldContain multiAllergenItem
                result shouldNotContain milkItem
                result shouldNotContain noAllergenItem
            }
        }

        When("finding items with an allergen that no item contains") {
            val result = service.findItemsWithAllergen(Allergen.PEANUTS)

            Then("it should return empty list") {
                result.shouldBeEmpty()
            }
        }

        When("finding items containing any of multiple allergens") {
            val result = service.findItemsContainingAnyAllergen(setOf(Allergen.MILK, Allergen.SULPHITES))

            Then("it should return items containing any of those allergens") {
                result shouldHaveSize 2
                result shouldContain milkItem
                result shouldContain multiAllergenItem
                result shouldNotContain glutenItem
                result shouldNotContain noAllergenItem
            }
        }

        When("finding items with empty allergen set") {
            val result = service.findItemsContainingAnyAllergen(emptySet())

            Then("it should return empty list") {
                result.shouldBeEmpty()
            }
        }
    }

    Given("an empty repository") {
        val repository = InMemoryStockRepository()
        val service = StockQueryService(repository)

        When("finding low stock items") {
            val result = service.findLowStockItems()

            Then("it should return empty list") {
                result.shouldBeEmpty()
            }
        }

        When("finding items with allergen") {
            val result = service.findItemsWithAllergen(Allergen.GLUTEN)

            Then("it should return empty list") {
                result.shouldBeEmpty()
            }
        }
    }
})
