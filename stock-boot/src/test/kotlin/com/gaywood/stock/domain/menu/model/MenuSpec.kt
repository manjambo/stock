package com.gaywood.stock.domain.menu.model

import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockCategory
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.model.StockItemId
import com.gaywood.stock.domain.stock.model.Unit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class MenuSpec : BehaviorSpec({

    Given("a Menu with items containing different allergens") {
        val glutenItem = MenuItem.create(
            name = "Beer",
            price = Price(BigDecimal("5.00")),
            cachedAllergens = setOf(Allergen.GLUTEN)
        )
        val milkItem = MenuItem.create(
            name = "Latte",
            price = Price(BigDecimal("3.50")),
            cachedAllergens = setOf(Allergen.MILK)
        )
        val multiAllergenItem = MenuItem.create(
            name = "Fish & Chips",
            price = Price(BigDecimal("12.00")),
            cachedAllergens = setOf(Allergen.FISH, Allergen.GLUTEN)
        )
        val noAllergenItem = MenuItem.create(
            name = "Water",
            price = Price(BigDecimal("1.00")),
            cachedAllergens = emptySet()
        )

        val menu = Menu.create(
            name = "Test Menu",
            type = MenuType.BAR,
            items = listOf(glutenItem, milkItem, multiAllergenItem, noAllergenItem)
        )

        When("collecting all allergens") {
            val result = menu.collectAllAllergens()

            Then("it should return all unique allergens from all items") {
                result shouldContainExactlyInAnyOrder setOf(Allergen.GLUTEN, Allergen.MILK, Allergen.FISH)
            }
        }

        When("finding items containing GLUTEN") {
            val result = menu.findItemsContainingAllergen(Allergen.GLUTEN)

            Then("it should return items with gluten") {
                result shouldHaveSize 2
                result shouldContain glutenItem
                result shouldContain multiAllergenItem
                result shouldNotContain milkItem
                result shouldNotContain noAllergenItem
            }
        }

        When("finding items containing an allergen no item has") {
            val result = menu.findItemsContainingAllergen(Allergen.PEANUTS)

            Then("it should return empty list") {
                result.shouldBeEmpty()
            }
        }

        When("finding items free of GLUTEN") {
            val result = menu.findItemsFreeOfAllergens(setOf(Allergen.GLUTEN))

            Then("it should return items without gluten") {
                result shouldHaveSize 2
                result shouldContain milkItem
                result shouldContain noAllergenItem
                result shouldNotContain glutenItem
                result shouldNotContain multiAllergenItem
            }
        }

        When("finding items free of GLUTEN and MILK") {
            val result = menu.findItemsFreeOfAllergens(setOf(Allergen.GLUTEN, Allergen.MILK))

            Then("it should return only items without either allergen") {
                result shouldHaveSize 1
                result shouldContain noAllergenItem
            }
        }

        When("finding items free of empty allergen set") {
            val result = menu.findItemsFreeOfAllergens(emptySet())

            Then("it should return all items") {
                result shouldHaveSize 4
            }
        }
    }

    Given("a Menu with items that need allergen refresh") {
        val ginId = StockItemId.generate()
        val tonicId = StockItemId.generate()

        val ginAndTonic = MenuItem.create(
            name = "Gin & Tonic",
            price = Price(BigDecimal("5.00")),
            ingredients = listOf(
                MenuItemIngredient(ginId, Quantity(50.0, Unit.MILLILITERS)),
                MenuItemIngredient(tonicId, Quantity(150.0, Unit.MILLILITERS))
            )
        )
        val pureGin = MenuItem.create(
            name = "Gin Shot",
            price = Price(BigDecimal("3.00")),
            ingredients = listOf(
                MenuItemIngredient(ginId, Quantity(25.0, Unit.MILLILITERS))
            )
        )

        val menu = Menu.create(
            name = "Drinks",
            type = MenuType.BAR,
            items = listOf(ginAndTonic, pureGin)
        )

        val gin = StockItem.create(
            id = ginId,
            name = "Gin",
            category = StockCategory.Bar.Spirits,
            initialQuantity = Quantity(1.0, Unit.LITERS),
            allergens = setOf(Allergen.GLUTEN)
        )
        val tonic = StockItem.create(
            id = tonicId,
            name = "Tonic Water",
            category = StockCategory.Bar.Mixers,
            initialQuantity = Quantity(2.0, Unit.LITERS),
            allergens = setOf(Allergen.SULPHITES)
        )

        When("refreshing all allergens") {
            menu.refreshAllAllergens(mapOf(ginId to gin, tonicId to tonic))

            Then("all menu items should have updated allergens") {
                ginAndTonic.allergens shouldContainExactlyInAnyOrder setOf(Allergen.GLUTEN, Allergen.SULPHITES)
                pureGin.allergens shouldContainExactlyInAnyOrder setOf(Allergen.GLUTEN)
            }

            Then("collectAllAllergens should reflect updated data") {
                menu.collectAllAllergens() shouldContainExactlyInAnyOrder setOf(Allergen.GLUTEN, Allergen.SULPHITES)
            }
        }
    }

    Given("an empty Menu") {
        val menu = Menu.create(
            name = "Empty Menu",
            type = MenuType.FOOD,
            items = emptyList()
        )

        When("collecting all allergens") {
            val result = menu.collectAllAllergens()

            Then("it should return empty set") {
                result.shouldBeEmpty()
            }
        }

        When("finding items containing allergen") {
            val result = menu.findItemsContainingAllergen(Allergen.GLUTEN)

            Then("it should return empty list") {
                result.shouldBeEmpty()
            }
        }

        When("finding items free of allergens") {
            val result = menu.findItemsFreeOfAllergens(setOf(Allergen.GLUTEN))

            Then("it should return empty list") {
                result.shouldBeEmpty()
            }
        }
    }

    Given("Menu validation") {
        Then("blank name should throw exception") {
            try {
                Menu.create(name = "", type = MenuType.BAR)
                throw AssertionError("Expected IllegalArgumentException")
            } catch (e: IllegalArgumentException) {
                e.message shouldBe "Menu name cannot be blank"
            }
        }
    }
})
