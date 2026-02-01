package com.gaywood.stock.fixtures

import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.LowStockThreshold
import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockCategory
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.model.StockItemId
import com.gaywood.stock.domain.stock.model.Unit

object StockFixtures {

    fun vodkaBottle(
        id: StockItemId = StockItemId.generate(),
        quantity: Int = 10,
        lowStockThreshold: Int? = 5,
        allergens: Set<Allergen> = emptySet()
    ): StockItem = StockItem.create(
        id = id,
        name = "Absolut Vodka",
        category = StockCategory.Bar.Spirits,
        initialQuantity = Quantity(quantity, Unit.BOTTLES),
        lowStockThreshold = lowStockThreshold?.let { LowStockThreshold(Quantity(it, Unit.BOTTLES)) },
        allergens = allergens
    )

    fun redWine(
        id: StockItemId = StockItemId.generate(),
        quantity: Int = 24,
        lowStockThreshold: Int? = 6,
        allergens: Set<Allergen> = setOf(Allergen.SULPHITES)
    ): StockItem = StockItem.create(
        id = id,
        name = "Cabernet Sauvignon",
        category = StockCategory.Bar.Wine,
        initialQuantity = Quantity(quantity, Unit.BOTTLES),
        lowStockThreshold = lowStockThreshold?.let { LowStockThreshold(Quantity(it, Unit.BOTTLES)) },
        allergens = allergens
    )

    fun craftBeer(
        id: StockItemId = StockItemId.generate(),
        quantity: Int = 48,
        allergens: Set<Allergen> = setOf(Allergen.GLUTEN)
    ): StockItem = StockItem.create(
        id = id,
        name = "IPA Craft Beer",
        category = StockCategory.Bar.Beer,
        initialQuantity = Quantity(quantity, Unit.BOTTLES),
        allergens = allergens
    )

    fun tonicWater(
        id: StockItemId = StockItemId.generate(),
        quantity: Int = 36,
        allergens: Set<Allergen> = emptySet()
    ): StockItem = StockItem.create(
        id = id,
        name = "Fever Tree Tonic",
        category = StockCategory.Bar.Mixers,
        initialQuantity = Quantity(quantity, Unit.BOTTLES),
        allergens = allergens
    )

    fun lemons(
        id: StockItemId = StockItemId.generate(),
        quantity: Int = 50,
        allergens: Set<Allergen> = emptySet()
    ): StockItem = StockItem.create(
        id = id,
        name = "Fresh Lemons",
        category = StockCategory.Bar.Garnishes,
        initialQuantity = Quantity(quantity, Unit.PIECES),
        allergens = allergens
    )

    fun chickenBreast(
        id: StockItemId = StockItemId.generate(),
        quantity: Double = 10.0,
        lowStockThreshold: Double? = 2.0,
        allergens: Set<Allergen> = emptySet()
    ): StockItem = StockItem.create(
        id = id,
        name = "Chicken Breast",
        category = StockCategory.Kitchen.Proteins,
        initialQuantity = Quantity(quantity, Unit.KILOGRAMS),
        lowStockThreshold = lowStockThreshold?.let { LowStockThreshold(Quantity(it, Unit.KILOGRAMS)) },
        allergens = allergens
    )

    fun tomatoes(
        id: StockItemId = StockItemId.generate(),
        quantity: Double = 5.0,
        allergens: Set<Allergen> = emptySet()
    ): StockItem = StockItem.create(
        id = id,
        name = "Roma Tomatoes",
        category = StockCategory.Kitchen.Vegetables,
        initialQuantity = Quantity(quantity, Unit.KILOGRAMS),
        allergens = allergens
    )

    fun butter(
        id: StockItemId = StockItemId.generate(),
        quantity: Double = 3.0,
        lowStockThreshold: Double? = 1.0,
        allergens: Set<Allergen> = setOf(Allergen.MILK)
    ): StockItem = StockItem.create(
        id = id,
        name = "Unsalted Butter",
        category = StockCategory.Kitchen.Dairy,
        initialQuantity = Quantity(quantity, Unit.KILOGRAMS),
        lowStockThreshold = lowStockThreshold?.let { LowStockThreshold(Quantity(it, Unit.KILOGRAMS)) },
        allergens = allergens
    )

    fun flour(
        id: StockItemId = StockItemId.generate(),
        quantity: Double = 25.0,
        allergens: Set<Allergen> = setOf(Allergen.GLUTEN)
    ): StockItem = StockItem.create(
        id = id,
        name = "All-Purpose Flour",
        category = StockCategory.Kitchen.DryGoods,
        initialQuantity = Quantity(quantity, Unit.KILOGRAMS),
        allergens = allergens
    )

    fun blackPepper(
        id: StockItemId = StockItemId.generate(),
        quantity: Int = 500,
        allergens: Set<Allergen> = emptySet()
    ): StockItem = StockItem.create(
        id = id,
        name = "Ground Black Pepper",
        category = StockCategory.Kitchen.Spices,
        initialQuantity = Quantity(quantity, Unit.GRAMS),
        allergens = allergens
    )

    fun frozenFries(
        id: StockItemId = StockItemId.generate(),
        quantity: Double = 15.0,
        allergens: Set<Allergen> = emptySet()
    ): StockItem = StockItem.create(
        id = id,
        name = "Frozen French Fries",
        category = StockCategory.Kitchen.Frozen,
        initialQuantity = Quantity(quantity, Unit.KILOGRAMS),
        allergens = allergens
    )

    // Additional fixtures with common allergens for testing
    fun shrimpCocktail(
        id: StockItemId = StockItemId.generate(),
        quantity: Double = 5.0,
        allergens: Set<Allergen> = setOf(Allergen.CRUSTACEANS)
    ): StockItem = StockItem.create(
        id = id,
        name = "Shrimp Cocktail",
        category = StockCategory.Kitchen.Proteins,
        initialQuantity = Quantity(quantity, Unit.KILOGRAMS),
        allergens = allergens
    )

    fun scrambledEggs(
        id: StockItemId = StockItemId.generate(),
        quantity: Int = 60,
        allergens: Set<Allergen> = setOf(Allergen.EGGS)
    ): StockItem = StockItem.create(
        id = id,
        name = "Fresh Eggs",
        category = StockCategory.Kitchen.Dairy,
        initialQuantity = Quantity(quantity, Unit.PIECES),
        allergens = allergens
    )

    fun peanutButter(
        id: StockItemId = StockItemId.generate(),
        quantity: Double = 2.0,
        allergens: Set<Allergen> = setOf(Allergen.PEANUTS)
    ): StockItem = StockItem.create(
        id = id,
        name = "Peanut Butter",
        category = StockCategory.Kitchen.DryGoods,
        initialQuantity = Quantity(quantity, Unit.KILOGRAMS),
        allergens = allergens
    )

    fun soySauce(
        id: StockItemId = StockItemId.generate(),
        quantity: Double = 1.0,
        allergens: Set<Allergen> = setOf(Allergen.SOYBEANS, Allergen.GLUTEN)
    ): StockItem = StockItem.create(
        id = id,
        name = "Soy Sauce",
        category = StockCategory.Kitchen.Spices,
        initialQuantity = Quantity(quantity, Unit.LITERS),
        allergens = allergens
    )
}
