package com.gaywood.stock.infrastructure.seed

import com.gaywood.stock.domain.menu.model.*
import com.gaywood.stock.domain.menu.repository.MenuRepository
import com.gaywood.stock.domain.stock.model.*
import com.gaywood.stock.domain.stock.model.Unit as StockUnit
import com.gaywood.stock.domain.stock.repository.StockRepository
import java.math.BigDecimal

object BattleBoomBarSeeder {

    fun seed(stockRepository: StockRepository, menuRepository: MenuRepository) {
        val stockItems = seedStockItems(stockRepository)
        seedBarMenu(menuRepository, stockItems)
        seedFoodMenu(menuRepository, stockItems)
    }

    private fun seedStockItems(repository: StockRepository): Map<String, StockItem> {
        return createStockItems(repository)
    }

    private fun seedBarMenu(repository: MenuRepository, stockItems: Map<String, StockItem>) {
        repository.save(createBarMenu(stockItems))
    }

    private fun seedFoodMenu(repository: MenuRepository, stockItems: Map<String, StockItem>) {
        repository.save(createFoodMenu(stockItems))
    }

    private fun createStockItems(repository: StockRepository): Map<String, StockItem> {
        val items = createSpirits() +
            createMixers() +
            createBeers() +
            createWines() +
            createKitchenItems()

        items.values.forEach { repository.save(it) }
        return items
    }

    private fun createSpirits() = mapOf(
        "gin" to StockItem.create(
            name = "Tanqueray Gin",
            category = StockCategory.Bar.Spirits,
            initialQuantity = Quantity(10.0, StockUnit.LITERS)
        ),
        "vodka" to StockItem.create(
            name = "Absolut Vodka",
            category = StockCategory.Bar.Spirits,
            initialQuantity = Quantity(10.0, StockUnit.LITERS)
        ),
        "rum" to StockItem.create(
            name = "Captain Morgan Rum",
            category = StockCategory.Bar.Spirits,
            initialQuantity = Quantity(10.0, StockUnit.LITERS)
        ),
        "tequila" to StockItem.create(
            name = "Jose Cuervo Tequila",
            category = StockCategory.Bar.Spirits,
            initialQuantity = Quantity(10.0, StockUnit.LITERS)
        ),
        "whisky" to StockItem.create(
            name = "Jack Daniels",
            category = StockCategory.Bar.Spirits,
            initialQuantity = Quantity(10.0, StockUnit.LITERS)
        )
    )

    private fun createMixers() = mapOf(
        "tonic" to StockItem.create(
            name = "Fever Tree Tonic",
            category = StockCategory.Bar.Mixers,
            initialQuantity = Quantity(100.0, StockUnit.LITERS)
        ),
        "cola" to StockItem.create(
            name = "Coca Cola",
            category = StockCategory.Bar.Mixers,
            initialQuantity = Quantity(100.0, StockUnit.LITERS)
        ),
        "lemonade" to StockItem.create(
            name = "Schweppes Lemonade",
            category = StockCategory.Bar.Mixers,
            initialQuantity = Quantity(100.0, StockUnit.LITERS)
        ),
        "passoa" to StockItem.create(
            name = "Passoa Passion Fruit",
            category = StockCategory.Bar.Mixers,
            initialQuantity = Quantity(5.0, StockUnit.LITERS)
        )
    )

    private fun createBeers() = mapOf(
        "lager" to StockItem.create(
            name = "Peroni Nastro Azzurro",
            category = StockCategory.Bar.Beer,
            initialQuantity = Quantity(200.0, StockUnit.BOTTLES),
            allergens = setOf(Allergen.GLUTEN)
        ),
        "ipa" to StockItem.create(
            name = "Brewdog Punk IPA",
            category = StockCategory.Bar.Beer,
            initialQuantity = Quantity(200.0, StockUnit.BOTTLES),
            allergens = setOf(Allergen.GLUTEN)
        ),
        "cider" to StockItem.create(
            name = "Aspall Cyder",
            category = StockCategory.Bar.Beer,
            initialQuantity = Quantity(100.0, StockUnit.BOTTLES),
            allergens = setOf(Allergen.SULPHITES)
        )
    )

    private fun createWines() = mapOf(
        "prosecco" to StockItem.create(
            name = "House Prosecco",
            category = StockCategory.Bar.Wine,
            initialQuantity = Quantity(50.0, StockUnit.BOTTLES),
            allergens = setOf(Allergen.SULPHITES)
        ),
        "whitewine" to StockItem.create(
            name = "Pinot Grigio",
            category = StockCategory.Bar.Wine,
            initialQuantity = Quantity(30.0, StockUnit.BOTTLES),
            allergens = setOf(Allergen.SULPHITES)
        ),
        "redwine" to StockItem.create(
            name = "Malbec",
            category = StockCategory.Bar.Wine,
            initialQuantity = Quantity(30.0, StockUnit.BOTTLES),
            allergens = setOf(Allergen.SULPHITES)
        ),
        "rosewine" to StockItem.create(
            name = "Provence Rosé",
            category = StockCategory.Bar.Wine,
            initialQuantity = Quantity(30.0, StockUnit.BOTTLES),
            allergens = setOf(Allergen.SULPHITES)
        )
    )

    private fun createKitchenItems() = mapOf(
        "wings" to StockItem.create(
            name = "Chicken Wings",
            category = StockCategory.Kitchen.Proteins,
            initialQuantity = Quantity(50.0, StockUnit.KILOGRAMS)
        ),
        "nachos" to StockItem.create(
            name = "Tortilla Chips",
            category = StockCategory.Kitchen.DryGoods,
            initialQuantity = Quantity(20.0, StockUnit.KILOGRAMS),
            allergens = setOf(Allergen.GLUTEN)
        ),
        "cheese" to StockItem.create(
            name = "Nacho Cheese",
            category = StockCategory.Kitchen.Dairy,
            initialQuantity = Quantity(10.0, StockUnit.KILOGRAMS),
            allergens = setOf(Allergen.MILK)
        ),
        "slider_buns" to StockItem.create(
            name = "Slider Buns",
            category = StockCategory.Kitchen.DryGoods,
            initialQuantity = Quantity(500.0, StockUnit.PIECES),
            allergens = setOf(Allergen.GLUTEN, Allergen.EGGS)
        ),
        "beef_patties" to StockItem.create(
            name = "Beef Slider Patties",
            category = StockCategory.Kitchen.Proteins,
            initialQuantity = Quantity(500.0, StockUnit.PIECES)
        ),
        "halloumi" to StockItem.create(
            name = "Halloumi Cheese",
            category = StockCategory.Kitchen.Dairy,
            initialQuantity = Quantity(20.0, StockUnit.KILOGRAMS),
            allergens = setOf(Allergen.MILK)
        ),
        "fries" to StockItem.create(
            name = "French Fries",
            category = StockCategory.Kitchen.Frozen,
            initialQuantity = Quantity(50.0, StockUnit.KILOGRAMS)
        ),
        "pizza_base" to StockItem.create(
            name = "Flatbread Pizza Base",
            category = StockCategory.Kitchen.DryGoods,
            initialQuantity = Quantity(100.0, StockUnit.PIECES),
            allergens = setOf(Allergen.GLUTEN)
        ),
        "mozzarella" to StockItem.create(
            name = "Mozzarella",
            category = StockCategory.Kitchen.Dairy,
            initialQuantity = Quantity(10.0, StockUnit.KILOGRAMS),
            allergens = setOf(Allergen.MILK)
        )
    )

    private fun createBarMenu(stockItems: Map<String, StockItem>): Menu {
        val items = createCocktails(stockItems) +
            createSpiritMixers(stockItems) +
            createBeerMenuItems(stockItems) +
            createWineMenuItems(stockItems)

        return Menu.create(
            name = "battle.boom.bar.o2",
            description = "Battle Boom Bar O2 - Drinks Menu",
            type = MenuType.BAR,
            items = items
        )
    }

    private fun createCocktails(stockItems: Map<String, StockItem>) = listOf(
        MenuItem.create(
            name = "Pornstar Martini",
            description = "Absolut Vodka, Passoa, passion fruit, lime, vanilla, prosecco shot",
            price = Price(BigDecimal("11.50")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["vodka"]!!.id, Quantity(50.0, StockUnit.MILLILITERS)),
                MenuItemIngredient(stockItems["passoa"]!!.id, Quantity(25.0, StockUnit.MILLILITERS)),
                MenuItemIngredient(stockItems["prosecco"]!!.id, Quantity(50.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Espresso Martini",
            description = "Absolut Vodka, Kahlua, fresh espresso, sugar syrup",
            price = Price(BigDecimal("11.50")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["vodka"]!!.id, Quantity(50.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Long Island Iced Tea",
            description = "Vodka, gin, rum, tequila, triple sec, cola",
            price = Price(BigDecimal("12.00")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["vodka"]!!.id, Quantity(15.0, StockUnit.MILLILITERS)),
                MenuItemIngredient(stockItems["gin"]!!.id, Quantity(15.0, StockUnit.MILLILITERS)),
                MenuItemIngredient(stockItems["rum"]!!.id, Quantity(15.0, StockUnit.MILLILITERS)),
                MenuItemIngredient(stockItems["tequila"]!!.id, Quantity(15.0, StockUnit.MILLILITERS)),
                MenuItemIngredient(stockItems["cola"]!!.id, Quantity(100.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Mojito",
            description = "Captain Morgan White Rum, fresh mint, lime, sugar, soda",
            price = Price(BigDecimal("10.50")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["rum"]!!.id, Quantity(50.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Margarita",
            description = "Jose Cuervo, triple sec, fresh lime, salt rim",
            price = Price(BigDecimal("10.50")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["tequila"]!!.id, Quantity(50.0, StockUnit.MILLILITERS))
            )
        )
    )

    private fun createSpiritMixers(stockItems: Map<String, StockItem>) = listOf(
        MenuItem.create(
            name = "Gin & Tonic",
            description = "Tanqueray Gin with Fever Tree Tonic",
            price = Price(BigDecimal("8.50")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["gin"]!!.id, Quantity(50.0, StockUnit.MILLILITERS)),
                MenuItemIngredient(stockItems["tonic"]!!.id, Quantity(150.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Vodka & Coke",
            description = "Absolut Vodka with Coca Cola",
            price = Price(BigDecimal("8.00")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["vodka"]!!.id, Quantity(50.0, StockUnit.MILLILITERS)),
                MenuItemIngredient(stockItems["cola"]!!.id, Quantity(150.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Rum & Coke",
            description = "Captain Morgan with Coca Cola",
            price = Price(BigDecimal("8.00")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["rum"]!!.id, Quantity(50.0, StockUnit.MILLILITERS)),
                MenuItemIngredient(stockItems["cola"]!!.id, Quantity(150.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Jack & Coke",
            description = "Jack Daniels with Coca Cola",
            price = Price(BigDecimal("8.50")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["whisky"]!!.id, Quantity(50.0, StockUnit.MILLILITERS)),
                MenuItemIngredient(stockItems["cola"]!!.id, Quantity(150.0, StockUnit.MILLILITERS))
            )
        )
    )

    private fun createBeerMenuItems(stockItems: Map<String, StockItem>) = listOf(
        MenuItem.create(
            name = "Peroni (Bottle)",
            description = "330ml bottle of Peroni Nastro Azzurro",
            price = Price(BigDecimal("6.00")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["lager"]!!.id, Quantity(1.0, StockUnit.BOTTLES))
            )
        ),
        MenuItem.create(
            name = "Brewdog Punk IPA (Bottle)",
            description = "330ml bottle of Punk IPA",
            price = Price(BigDecimal("6.50")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["ipa"]!!.id, Quantity(1.0, StockUnit.BOTTLES))
            )
        ),
        MenuItem.create(
            name = "Aspall Cyder (Bottle)",
            description = "500ml bottle of Aspall Suffolk Cyder",
            price = Price(BigDecimal("6.50")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["cider"]!!.id, Quantity(1.0, StockUnit.BOTTLES))
            )
        )
    )

    private fun createWineMenuItems(stockItems: Map<String, StockItem>) = listOf(
        MenuItem.create(
            name = "Pinot Grigio (Glass)",
            description = "175ml glass of Italian Pinot Grigio",
            price = Price(BigDecimal("7.50")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["whitewine"]!!.id, Quantity(175.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Pinot Grigio (Bottle)",
            description = "750ml bottle of Italian Pinot Grigio",
            price = Price(BigDecimal("26.00")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["whitewine"]!!.id, Quantity(1.0, StockUnit.BOTTLES))
            )
        ),
        MenuItem.create(
            name = "Malbec (Glass)",
            description = "175ml glass of Argentinian Malbec",
            price = Price(BigDecimal("8.00")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["redwine"]!!.id, Quantity(175.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Malbec (Bottle)",
            description = "750ml bottle of Argentinian Malbec",
            price = Price(BigDecimal("28.00")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["redwine"]!!.id, Quantity(1.0, StockUnit.BOTTLES))
            )
        ),
        MenuItem.create(
            name = "Provence Rosé (Glass)",
            description = "175ml glass of French Provence Rosé",
            price = Price(BigDecimal("8.50")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["rosewine"]!!.id, Quantity(175.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Prosecco (Glass)",
            description = "125ml glass of Italian Prosecco",
            price = Price(BigDecimal("7.00")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["prosecco"]!!.id, Quantity(125.0, StockUnit.MILLILITERS))
            )
        ),
        MenuItem.create(
            name = "Prosecco (Bottle)",
            description = "750ml bottle of Italian Prosecco",
            price = Price(BigDecimal("30.00")),
            ingredients = listOf(
                MenuItemIngredient(stockItems["prosecco"]!!.id, Quantity(1.0, StockUnit.BOTTLES))
            )
        )
    )

    private fun createFoodMenu(stockItems: Map<String, StockItem>): Menu {
        val items = listOf(
            MenuItem.create(
                name = "BBQ Chicken Wings",
                description = "Crispy wings tossed in BBQ sauce, served with blue cheese dip",
                price = Price(BigDecimal("9.50")),
                ingredients = listOf(
                    MenuItemIngredient(stockItems["wings"]!!.id, Quantity(300.0, StockUnit.GRAMS))
                )
            ),
            MenuItem.create(
                name = "Buffalo Chicken Wings",
                description = "Spicy buffalo wings with ranch dip",
                price = Price(BigDecimal("9.50")),
                ingredients = listOf(
                    MenuItemIngredient(stockItems["wings"]!!.id, Quantity(300.0, StockUnit.GRAMS))
                )
            ),
            MenuItem.create(
                name = "Loaded Nachos",
                description = "Tortilla chips with cheese sauce, jalapeños, salsa, sour cream & guac",
                price = Price(BigDecimal("11.00")),
                ingredients = listOf(
                    MenuItemIngredient(stockItems["nachos"]!!.id, Quantity(200.0, StockUnit.GRAMS)),
                    MenuItemIngredient(stockItems["cheese"]!!.id, Quantity(100.0, StockUnit.GRAMS))
                )
            ),
            MenuItem.create(
                name = "Beef Sliders (3)",
                description = "Three mini beef burgers with cheese, lettuce, tomato & special sauce",
                price = Price(BigDecimal("12.00")),
                ingredients = listOf(
                    MenuItemIngredient(stockItems["slider_buns"]!!.id, Quantity(3.0, StockUnit.PIECES)),
                    MenuItemIngredient(stockItems["beef_patties"]!!.id, Quantity(3.0, StockUnit.PIECES)),
                    MenuItemIngredient(stockItems["cheese"]!!.id, Quantity(60.0, StockUnit.GRAMS))
                )
            ),
            MenuItem.create(
                name = "Halloumi Fries",
                description = "Crispy halloumi sticks with sweet chilli dip",
                price = Price(BigDecimal("8.50")),
                ingredients = listOf(
                    MenuItemIngredient(stockItems["halloumi"]!!.id, Quantity(200.0, StockUnit.GRAMS))
                )
            ),
            MenuItem.create(
                name = "Loaded Fries",
                description = "Seasoned fries topped with cheese sauce and bacon bits",
                price = Price(BigDecimal("7.50")),
                ingredients = listOf(
                    MenuItemIngredient(stockItems["fries"]!!.id, Quantity(300.0, StockUnit.GRAMS)),
                    MenuItemIngredient(stockItems["cheese"]!!.id, Quantity(80.0, StockUnit.GRAMS))
                )
            ),
            MenuItem.create(
                name = "Margherita Flatbread",
                description = "Tomato sauce, mozzarella, fresh basil",
                price = Price(BigDecimal("10.00")),
                ingredients = listOf(
                    MenuItemIngredient(stockItems["pizza_base"]!!.id, Quantity(1.0, StockUnit.PIECES)),
                    MenuItemIngredient(stockItems["mozzarella"]!!.id, Quantity(150.0, StockUnit.GRAMS))
                )
            ),
            MenuItem.create(
                name = "Pepperoni Flatbread",
                description = "Tomato sauce, mozzarella, pepperoni",
                price = Price(BigDecimal("11.00")),
                ingredients = listOf(
                    MenuItemIngredient(stockItems["pizza_base"]!!.id, Quantity(1.0, StockUnit.PIECES)),
                    MenuItemIngredient(stockItems["mozzarella"]!!.id, Quantity(150.0, StockUnit.GRAMS))
                )
            ),
            MenuItem.create(
                name = "Classic Fries",
                description = "Crispy seasoned fries",
                price = Price(BigDecimal("5.00")),
                ingredients = listOf(
                    MenuItemIngredient(stockItems["fries"]!!.id, Quantity(250.0, StockUnit.GRAMS))
                )
            ),
            MenuItem.create(
                name = "BOOM Sharing Platter",
                description = "Wings, nachos, sliders, halloumi fries & loaded fries - perfect for sharing",
                price = Price(BigDecimal("35.00")),
                ingredients = listOf(
                    MenuItemIngredient(stockItems["wings"]!!.id, Quantity(200.0, StockUnit.GRAMS)),
                    MenuItemIngredient(stockItems["nachos"]!!.id, Quantity(150.0, StockUnit.GRAMS)),
                    MenuItemIngredient(stockItems["slider_buns"]!!.id, Quantity(2.0, StockUnit.PIECES)),
                    MenuItemIngredient(stockItems["beef_patties"]!!.id, Quantity(2.0, StockUnit.PIECES)),
                    MenuItemIngredient(stockItems["halloumi"]!!.id, Quantity(150.0, StockUnit.GRAMS)),
                    MenuItemIngredient(stockItems["fries"]!!.id, Quantity(300.0, StockUnit.GRAMS)),
                    MenuItemIngredient(stockItems["cheese"]!!.id, Quantity(150.0, StockUnit.GRAMS))
                )
            )
        )

        return Menu.create(
            name = "battle.boom.bar.o2.food",
            description = "Battle Boom Bar O2 - BOOM Bites Street Food",
            type = MenuType.FOOD,
            items = items
        )
    }
}
