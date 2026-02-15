package com.gaywood.stock.domain.menu.model

import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockCategory
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.model.StockItemId
import com.gaywood.stock.domain.stock.model.Unit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class MenuItemSpec : BehaviorSpec({

    Given("a MenuItem with ingredients") {
        val ginId = StockItemId.generate()
        val tonicId = StockItemId.generate()

        val menuItem = MenuItem.create(
            name = "Gin & Tonic",
            price = Price(BigDecimal("5.00")),
            ingredients = listOf(
                MenuItemIngredient(ginId, Quantity(50.0, Unit.MILLILITERS)),
                MenuItemIngredient(tonicId, Quantity(150.0, Unit.MILLILITERS))
            )
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
        val stockItemsById = mapOf(ginId to gin, tonicId to tonic)

        When("allergens are not yet cached") {
            Then("allergens should be empty") {
                menuItem.allergens.shouldBeEmpty()
            }
        }

        When("refreshing allergens from stock items") {
            menuItem.refreshAllergens(stockItemsById)

            Then("allergens should contain allergens from all ingredients") {
                menuItem.allergens shouldContainExactlyInAnyOrder setOf(Allergen.GLUTEN, Allergen.SULPHITES)
            }
        }
    }

    Given("a MenuItem created with cached allergens") {
        val menuItem = MenuItem.create(
            name = "Test Item",
            price = Price(BigDecimal("10.00")),
            cachedAllergens = setOf(Allergen.MILK, Allergen.EGGS)
        )

        Then("allergens should be available immediately") {
            menuItem.allergens shouldContainExactlyInAnyOrder setOf(Allergen.MILK, Allergen.EGGS)
        }
    }

    Given("a MenuItem with setCachedAllergens") {
        val menuItem = MenuItem.create(
            name = "Test Item",
            price = Price(BigDecimal("10.00"))
        )

        When("setting cached allergens directly") {
            menuItem.setCachedAllergens(setOf(Allergen.FISH, Allergen.CRUSTACEANS))

            Then("allergens should be updated") {
                menuItem.allergens shouldContainExactlyInAnyOrder setOf(Allergen.FISH, Allergen.CRUSTACEANS)
            }
        }
    }

    Given("a MenuItem with ingredients where some stock items are missing") {
        val existingId = StockItemId.generate()
        val missingId = StockItemId.generate()

        val menuItem = MenuItem.create(
            name = "Test Item",
            price = Price(BigDecimal("10.00")),
            ingredients = listOf(
                MenuItemIngredient(existingId, Quantity(1.0, Unit.PIECES)),
                MenuItemIngredient(missingId, Quantity(1.0, Unit.PIECES))
            )
        )

        val existingItem = StockItem.create(
            id = existingId,
            name = "Existing",
            category = StockCategory.Kitchen.Proteins,
            initialQuantity = Quantity(1.0, Unit.KILOGRAMS),
            allergens = setOf(Allergen.EGGS)
        )

        When("refreshing allergens with partial stock items") {
            menuItem.refreshAllergens(mapOf(existingId to existingItem))

            Then("allergens should only include allergens from found stock items") {
                menuItem.allergens shouldContainExactlyInAnyOrder setOf(Allergen.EGGS)
            }
        }
    }

    Given("MenuItem validation") {
        Then("blank name should throw exception") {
            try {
                MenuItem.create(name = "", price = Price(BigDecimal("5.00")))
                throw AssertionError("Expected IllegalArgumentException")
            } catch (e: IllegalArgumentException) {
                e.message shouldBe "Menu item name cannot be blank"
            }
        }
    }
})
