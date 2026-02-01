package com.gaywood.stock.domain.stock.model

import com.gaywood.stock.domain.stock.event.StockEvent
import com.gaywood.stock.fixtures.StockFixtures
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class AllergenSpec : BehaviorSpec({

    Given("a stock item without allergens") {
        val vodka = StockFixtures.vodkaBottle(allergens = emptySet())

        Then("allergens should be empty") {
            vodka.allergens.shouldBeEmpty()
        }

        Then("containsAllergen should return false for any allergen") {
            vodka.containsAllergen(Allergen.GLUTEN) shouldBe false
            vodka.containsAllergen(Allergen.MILK) shouldBe false
        }
    }

    Given("a stock item with allergens") {
        val beer = StockFixtures.craftBeer(allergens = setOf(Allergen.GLUTEN))

        Then("allergens should contain the specified allergen") {
            beer.allergens shouldHaveSize 1
            beer.allergens shouldContain Allergen.GLUTEN
        }

        Then("containsAllergen should return true for contained allergen") {
            beer.containsAllergen(Allergen.GLUTEN) shouldBe true
        }

        Then("containsAllergen should return false for non-contained allergen") {
            beer.containsAllergen(Allergen.MILK) shouldBe false
        }
    }

    Given("a stock item with multiple allergens") {
        val soySauce = StockFixtures.soySauce(allergens = setOf(Allergen.SOYBEANS, Allergen.GLUTEN))

        Then("allergens should contain all specified allergens") {
            soySauce.allergens shouldHaveSize 2
            soySauce.allergens shouldContainExactlyInAnyOrder listOf(Allergen.SOYBEANS, Allergen.GLUTEN)
        }
    }

    Given("adding an allergen to a stock item") {
        val chicken = StockFixtures.chickenBreast(allergens = emptySet())
        chicken.clearEvents()

        When("adding a new allergen") {
            chicken.addAllergen(Allergen.SESAME)

            Then("the allergen should be added") {
                chicken.allergens shouldContain Allergen.SESAME
            }

            Then("an AllergensUpdated event should be raised") {
                chicken.domainEvents shouldHaveSize 1
                val event = chicken.domainEvents.first()
                event.shouldBeInstanceOf<StockEvent.AllergensUpdated>()
                (event as StockEvent.AllergensUpdated).allergens shouldContain Allergen.SESAME
            }
        }
    }

    Given("adding a duplicate allergen") {
        val butter = StockFixtures.butter(allergens = setOf(Allergen.MILK))
        butter.clearEvents()

        When("adding the same allergen again") {
            butter.addAllergen(Allergen.MILK)

            Then("the allergens should remain unchanged") {
                butter.allergens shouldHaveSize 1
            }

            Then("no event should be raised") {
                butter.domainEvents.shouldBeEmpty()
            }
        }
    }

    Given("removing an allergen from a stock item") {
        val beer = StockFixtures.craftBeer(allergens = setOf(Allergen.GLUTEN))
        beer.clearEvents()

        When("removing an existing allergen") {
            beer.removeAllergen(Allergen.GLUTEN)

            Then("the allergen should be removed") {
                beer.allergens.shouldBeEmpty()
            }

            Then("an AllergensUpdated event should be raised") {
                beer.domainEvents shouldHaveSize 1
                val event = beer.domainEvents.first()
                event.shouldBeInstanceOf<StockEvent.AllergensUpdated>()
                (event as StockEvent.AllergensUpdated).allergens.shouldBeEmpty()
            }
        }
    }

    Given("removing a non-existent allergen") {
        val tomatoes = StockFixtures.tomatoes(allergens = emptySet())
        tomatoes.clearEvents()

        When("removing an allergen that doesn't exist") {
            tomatoes.removeAllergen(Allergen.GLUTEN)

            Then("no event should be raised") {
                tomatoes.domainEvents.shouldBeEmpty()
            }
        }
    }

    Given("updating allergens on a stock item") {
        val item = StockFixtures.flour(allergens = setOf(Allergen.GLUTEN))
        item.clearEvents()

        When("updating with a new set of allergens") {
            item.updateAllergens(setOf(Allergen.GLUTEN, Allergen.SESAME))

            Then("allergens should be updated") {
                item.allergens shouldHaveSize 2
                item.allergens shouldContainExactlyInAnyOrder listOf(Allergen.GLUTEN, Allergen.SESAME)
            }

            Then("an AllergensUpdated event should be raised") {
                item.domainEvents shouldHaveSize 1
            }
        }
    }

    Given("updating with the same allergens") {
        val item = StockFixtures.butter(allergens = setOf(Allergen.MILK))
        item.clearEvents()

        When("updating with identical allergens") {
            item.updateAllergens(setOf(Allergen.MILK))

            Then("no event should be raised") {
                item.domainEvents.shouldBeEmpty()
            }
        }
    }

    Given("all 14 major allergens") {
        Then("all allergens should be available in the enum") {
            Allergen.entries shouldHaveSize 14
            Allergen.entries.map { it.name } shouldContainExactlyInAnyOrder listOf(
                "CELERY",
                "GLUTEN",
                "CRUSTACEANS",
                "EGGS",
                "FISH",
                "LUPIN",
                "MILK",
                "MOLLUSCS",
                "MUSTARD",
                "TREE_NUTS",
                "PEANUTS",
                "SESAME",
                "SOYBEANS",
                "SULPHITES"
            )
        }

        Then("all allergens should have display names") {
            Allergen.entries.forEach { allergen ->
                allergen.displayName.isNotBlank() shouldBe true
            }
        }
    }
})
