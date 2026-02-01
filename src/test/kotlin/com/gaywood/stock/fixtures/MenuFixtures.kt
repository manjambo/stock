package com.gaywood.stock.fixtures

import com.gaywood.stock.domain.menu.model.*
import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockItemId
import com.gaywood.stock.domain.stock.model.Unit
import java.math.BigDecimal

object MenuFixtures {

    fun gin(id: StockItemId = StockItemId.generate()): StockItemId = id
    fun tonic(id: StockItemId = StockItemId.generate()): StockItemId = id
    fun fish(id: StockItemId = StockItemId.generate()): StockItemId = id
    fun chips(id: StockItemId = StockItemId.generate()): StockItemId = id
    fun sausage(id: StockItemId = StockItemId.generate()): StockItemId = id
    fun mash(id: StockItemId = StockItemId.generate()): StockItemId = id

    // ============== BAR DRINKS (Alcoholic) ==============

    fun ginAndTonic(
        id: MenuItemId = MenuItemId.generate(),
        ginStockId: StockItemId = StockItemId.generate(),
        tonicStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("4.50")),
        available: Boolean = true
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Gin & Tonic",
        description = "Classic G&T with premium gin and Fever Tree tonic",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(ginStockId, Quantity(50.0, Unit.MILLILITERS)),
            MenuItemIngredient(tonicStockId, Quantity(150.0, Unit.MILLILITERS))
        ),
        available = available
    )

    fun houseGin(
        id: MenuItemId = MenuItemId.generate(),
        ginStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("3.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Gin",
        description = "House gin, 25ml measure",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(ginStockId, Quantity(25.0, Unit.MILLILITERS))
        )
    )

    fun tonicWater(
        id: MenuItemId = MenuItemId.generate(),
        tonicStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("1.00"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Tonic",
        description = "Fever Tree tonic water",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(tonicStockId, Quantity(200.0, Unit.MILLILITERS))
        )
    )

    fun houseWineWhite(
        id: MenuItemId = MenuItemId.generate(),
        wineStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("5.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "House White Wine",
        description = "Crisp Sauvignon Blanc, 175ml glass",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(wineStockId, Quantity(175.0, Unit.MILLILITERS))
        )
    )

    fun houseWineRed(
        id: MenuItemId = MenuItemId.generate(),
        wineStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("5.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "House Red Wine",
        description = "Merlot, 175ml glass",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(wineStockId, Quantity(175.0, Unit.MILLILITERS))
        )
    )

    fun craftBeerPint(
        id: MenuItemId = MenuItemId.generate(),
        beerStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("5.80"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Craft IPA Pint",
        description = "Local craft IPA, full pint",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(beerStockId, Quantity(568.0, Unit.MILLILITERS))
        )
    )

    fun proseccoGlass(
        id: MenuItemId = MenuItemId.generate(),
        proseccoStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("6.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Prosecco",
        description = "Italian sparkling wine, 125ml flute",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(proseccoStockId, Quantity(125.0, Unit.MILLILITERS))
        )
    )

    // ============== ALCOHOL-FREE DRINKS ==============

    fun virginMojito(
        id: MenuItemId = MenuItemId.generate(),
        limeStockId: StockItemId = StockItemId.generate(),
        mintStockId: StockItemId = StockItemId.generate(),
        sodaStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("4.00"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Virgin Mojito",
        description = "Fresh lime, mint, soda - refreshingly alcohol-free",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(limeStockId, Quantity(30.0, Unit.MILLILITERS)),
            MenuItemIngredient(mintStockId, Quantity(10.0, Unit.GRAMS)),
            MenuItemIngredient(sodaStockId, Quantity(150.0, Unit.MILLILITERS))
        )
    )

    fun alcoholFreeIPA(
        id: MenuItemId = MenuItemId.generate(),
        afBeerStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("4.20"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Alcohol-Free IPA",
        description = "Full-flavoured craft IPA, 0.5% ABV",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(afBeerStockId, Quantity(330.0, Unit.MILLILITERS))
        )
    )

    fun sparklingElderflower(
        id: MenuItemId = MenuItemId.generate(),
        elderflowerStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("3.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Sparkling Elderflower",
        description = "Refreshing elderflower pressé",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(elderflowerStockId, Quantity(275.0, Unit.MILLILITERS))
        )
    )

    fun freshOrangeJuice(
        id: MenuItemId = MenuItemId.generate(),
        orangeStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("3.00"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Fresh Orange Juice",
        description = "Freshly squeezed orange juice",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(orangeStockId, Quantity(250.0, Unit.MILLILITERS))
        )
    )

    // ============== FOOD - PESCATARIAN ==============

    fun fishAndChips(
        id: MenuItemId = MenuItemId.generate(),
        fishStockId: StockItemId = StockItemId.generate(),
        chipsStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("14.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Fish and Chips",
        description = "Beer battered cod with hand-cut chips and mushy peas",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(fishStockId, Quantity(200.0, Unit.GRAMS)),
            MenuItemIngredient(chipsStockId, Quantity(250.0, Unit.GRAMS))
        )
    )

    fun grilledSalmonSalad(
        id: MenuItemId = MenuItemId.generate(),
        salmonStockId: StockItemId = StockItemId.generate(),
        saladStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("16.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Grilled Salmon Salad",
        description = "Scottish salmon fillet on mixed leaves with lemon dressing",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(salmonStockId, Quantity(180.0, Unit.GRAMS)),
            MenuItemIngredient(saladStockId, Quantity(100.0, Unit.GRAMS))
        )
    )

    fun prawnLinguine(
        id: MenuItemId = MenuItemId.generate(),
        prawnStockId: StockItemId = StockItemId.generate(),
        pastaStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("15.00"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Prawn Linguine",
        description = "King prawns with garlic, chilli and white wine sauce",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(prawnStockId, Quantity(150.0, Unit.GRAMS)),
            MenuItemIngredient(pastaStockId, Quantity(120.0, Unit.GRAMS))
        )
    )

    fun vegetableCurry(
        id: MenuItemId = MenuItemId.generate(),
        vegStockId: StockItemId = StockItemId.generate(),
        riceStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("12.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Vegetable Thai Curry",
        description = "Mixed vegetables in coconut curry with jasmine rice (Vegan)",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(vegStockId, Quantity(250.0, Unit.GRAMS)),
            MenuItemIngredient(riceStockId, Quantity(150.0, Unit.GRAMS))
        )
    )

    fun haloumiBurger(
        id: MenuItemId = MenuItemId.generate(),
        haloumiStockId: StockItemId = StockItemId.generate(),
        bunStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("13.00"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Halloumi Burger",
        description = "Grilled halloumi with roasted peppers and tzatziki",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(haloumiStockId, Quantity(150.0, Unit.GRAMS)),
            MenuItemIngredient(bunStockId, Quantity(1.0, Unit.PIECES))
        )
    )

    fun sausageAndMash(
        id: MenuItemId = MenuItemId.generate(),
        sausageStockId: StockItemId = StockItemId.generate(),
        mashStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("13.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Sausage and Mash",
        description = "Cumberland sausages with creamy mash and onion gravy",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(sausageStockId, Quantity(200.0, Unit.GRAMS)),
            MenuItemIngredient(mashStockId, Quantity(300.0, Unit.GRAMS))
        )
    )

    // ============== BAR SNACKS ==============

    fun mixedNuts(
        id: MenuItemId = MenuItemId.generate(),
        nutsStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("3.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Mixed Nuts",
        description = "Salted mixed nuts",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(nutsStockId, Quantity(80.0, Unit.GRAMS))
        )
    )

    fun olives(
        id: MenuItemId = MenuItemId.generate(),
        olivesStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("4.00"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Marinated Olives",
        description = "Mixed olives with herbs and garlic",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(olivesStockId, Quantity(100.0, Unit.GRAMS))
        )
    )

    fun nachos(
        id: MenuItemId = MenuItemId.generate(),
        nachosStockId: StockItemId = StockItemId.generate(),
        cheeseStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("7.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Loaded Nachos",
        description = "Tortilla chips with cheese, salsa, guacamole and sour cream",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(nachosStockId, Quantity(150.0, Unit.GRAMS)),
            MenuItemIngredient(cheeseStockId, Quantity(50.0, Unit.GRAMS))
        )
    )

    fun chickenWings(
        id: MenuItemId = MenuItemId.generate(),
        wingsStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("8.00"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Buffalo Wings",
        description = "Crispy chicken wings with hot sauce and blue cheese dip",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(wingsStockId, Quantity(300.0, Unit.GRAMS))
        )
    )

    fun halloumiSticks(
        id: MenuItemId = MenuItemId.generate(),
        haloumiStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("6.50"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Halloumi Sticks",
        description = "Crispy halloumi fries with sweet chilli dip",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(haloumiStockId, Quantity(120.0, Unit.GRAMS))
        )
    )

    // ============== SHARING PLATTERS ==============

    fun sharingPlatterMediterranean(
        id: MenuItemId = MenuItemId.generate(),
        hummusStockId: StockItemId = StockItemId.generate(),
        falafelStockId: StockItemId = StockItemId.generate(),
        pitaStockId: StockItemId = StockItemId.generate(),
        olivesStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("18.00"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Mediterranean Sharing Platter",
        description = "Hummus, falafel, warm pitta bread, olives and tzatziki (Vegetarian)",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(hummusStockId, Quantity(150.0, Unit.GRAMS)),
            MenuItemIngredient(falafelStockId, Quantity(200.0, Unit.GRAMS)),
            MenuItemIngredient(pitaStockId, Quantity(4.0, Unit.PIECES)),
            MenuItemIngredient(olivesStockId, Quantity(100.0, Unit.GRAMS))
        )
    )

    fun seafoodPlatter(
        id: MenuItemId = MenuItemId.generate(),
        prawnStockId: StockItemId = StockItemId.generate(),
        calamariStockId: StockItemId = StockItemId.generate(),
        fishStockId: StockItemId = StockItemId.generate(),
        price: Price = Price(BigDecimal("24.00"))
    ): MenuItem = MenuItem.create(
        id = id,
        name = "Seafood Sharing Platter",
        description = "Grilled prawns, calamari, fish goujons with aioli and lemon",
        price = price,
        ingredients = listOf(
            MenuItemIngredient(prawnStockId, Quantity(200.0, Unit.GRAMS)),
            MenuItemIngredient(calamariStockId, Quantity(150.0, Unit.GRAMS)),
            MenuItemIngredient(fishStockId, Quantity(150.0, Unit.GRAMS))
        )
    )

    // ============== MENU BUILDERS ==============

    fun barMenu(
        id: MenuId = MenuId.generate(),
        items: List<MenuItem> = emptyList(),
        active: Boolean = true
    ): Menu = Menu.create(
        id = id,
        name = "Bar Menu",
        description = "Drinks and spirits",
        type = MenuType.BAR,
        items = items,
        active = active
    )

    fun foodMenu(
        id: MenuId = MenuId.generate(),
        items: List<MenuItem> = emptyList(),
        active: Boolean = true
    ): Menu = Menu.create(
        id = id,
        name = "Food Menu",
        description = "Main courses and sides",
        type = MenuType.FOOD,
        items = items,
        active = active
    )

    fun completeBarMenu(
        ginStockId: StockItemId = StockItemId.generate(),
        tonicStockId: StockItemId = StockItemId.generate()
    ): Menu {
        val gin = houseGin(ginStockId = ginStockId)
        val tonic = tonicWater(tonicStockId = tonicStockId)
        return barMenu(items = listOf(gin, tonic))
    }

    fun completeFoodMenu(
        fishStockId: StockItemId = StockItemId.generate(),
        chipsStockId: StockItemId = StockItemId.generate(),
        sausageStockId: StockItemId = StockItemId.generate(),
        mashStockId: StockItemId = StockItemId.generate()
    ): Menu {
        val fishAndChips = fishAndChips(fishStockId = fishStockId, chipsStockId = chipsStockId)
        val sausageMash = sausageAndMash(sausageStockId = sausageStockId, mashStockId = mashStockId)
        return foodMenu(items = listOf(fishAndChips, sausageMash))
    }
}
