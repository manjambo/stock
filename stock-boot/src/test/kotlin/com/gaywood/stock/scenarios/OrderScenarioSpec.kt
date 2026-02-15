package com.gaywood.stock.scenarios

import com.gaywood.stock.application.OrderService
import com.gaywood.stock.domain.menu.model.MenuItem
import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.fixtures.MenuFixtures
import com.gaywood.stock.fixtures.StaffFixtures
import com.gaywood.stock.infrastructure.persistence.InMemoryMenuRepository
import com.gaywood.stock.infrastructure.persistence.InMemoryOrderRepository
import com.gaywood.stock.infrastructure.persistence.InMemoryStaffRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.math.BigDecimal

/**
 * Real-world ordering scenarios that demonstrate the system handling
 * typical customer groups and their orders.
 */
class OrderScenarioSpec : BehaviorSpec({

    // ==================================================================================
    // SCENARIO 1: Group of 6 pescatarians wanting drinks and food
    // ==================================================================================
    Given("Scenario 1: A group of 6 pescatarians ordering drinks and food") {
        val staffRepository = InMemoryStaffRepository()
        val menuRepository = InMemoryMenuRepository()
        val orderRepository = InMemoryOrderRepository()

        val waiter = StaffFixtures.barWorker(firstName = "Emma", lastName = "Server")
        staffRepository.save(waiter)

        // Create menu items with tracked IDs
        val ginAndTonicId = MenuItemId.generate()
        val whiteWineId = MenuItemId.generate()
        val proseccoId = MenuItemId.generate()
        val craftBeerId = MenuItemId.generate()
        val fishAndChipsId = MenuItemId.generate()
        val salmonSaladId = MenuItemId.generate()
        val prawnLinguineId = MenuItemId.generate()
        val vegCurryId = MenuItemId.generate()
        val haloumiBurgerId = MenuItemId.generate()

        val barMenu = MenuFixtures.barMenu(
            items = listOf(
                MenuFixtures.ginAndTonic(id = ginAndTonicId),
                MenuFixtures.houseWineWhite(id = whiteWineId),
                MenuFixtures.proseccoGlass(id = proseccoId),
                MenuFixtures.craftBeerPint(id = craftBeerId)
            )
        )

        val foodMenu = MenuFixtures.foodMenu(
            items = listOf(
                MenuFixtures.fishAndChips(id = fishAndChipsId),
                MenuFixtures.grilledSalmonSalad(id = salmonSaladId),
                MenuFixtures.prawnLinguine(id = prawnLinguineId),
                MenuFixtures.vegetableCurry(id = vegCurryId),
                MenuFixtures.haloumiBurger(id = haloumiBurgerId)
            )
        )

        menuRepository.save(barMenu)
        menuRepository.save(foodMenu)

        val orderService = OrderService(orderRepository, menuRepository, staffRepository)

        When("the group orders drinks to start") {
            /*
             * Order breakdown for 6 people:
             * - 2x Gin & Tonic @ £4.50 = £9.00
             * - 2x House White Wine @ £5.50 = £11.00
             * - 1x Prosecco @ £6.50 = £6.50
             * - 1x Craft IPA Pint @ £5.80 = £5.80
             * Total drinks: £32.30
             */
            val drinksOrder = orderService.placeOrder(
                staffId = waiter.id.value,
                tableNumber = 12,
                items = listOf(
                    OrderService.OrderItemInput(ginAndTonicId.value, 2),
                    OrderService.OrderItemInput(whiteWineId.value, 2),
                    OrderService.OrderItemInput(proseccoId.value, 1),
                    OrderService.OrderItemInput(craftBeerId.value, 1)
                )
            )

            Then("the drinks order is created with correct total") {
                drinksOrder.items.size shouldBe 4
                drinksOrder.totalAmount.amount shouldBe BigDecimal("32.30")

                val drinksBill = drinksOrder.generateBill()
                println("\n=== SCENARIO 1: Drinks Order ===")
                println(drinksBill.formatAsText())

                drinksBill.formatAsText() shouldContain "Table: 12"
                drinksBill.formatAsText() shouldContain "Gin & Tonic"
                drinksBill.formatAsText() shouldContain "£32.30"
            }
        }

        When("the group orders pescatarian food") {
            /*
             * Pescatarian food for 6 people:
             * - 2x Fish and Chips @ £14.50 = £29.00
             * - 1x Grilled Salmon Salad @ £16.50 = £16.50
             * - 1x Prawn Linguine @ £15.00 = £15.00
             * - 1x Vegetable Thai Curry @ £12.50 = £12.50
             * - 1x Halloumi Burger @ £13.00 = £13.00
             * Total food: £86.00
             */
            val foodOrder = orderService.placeOrder(
                staffId = waiter.id.value,
                tableNumber = 12,
                items = listOf(
                    OrderService.OrderItemInput(fishAndChipsId.value, 2),
                    OrderService.OrderItemInput(salmonSaladId.value, 1),
                    OrderService.OrderItemInput(prawnLinguineId.value, 1),
                    OrderService.OrderItemInput(vegCurryId.value, 1),
                    OrderService.OrderItemInput(haloumiBurgerId.value, 1)
                )
            )

            Then("the food order is created with correct total") {
                foodOrder.items.size shouldBe 5
                foodOrder.totalAmount.amount shouldBe BigDecimal("86.00")

                val foodBill = foodOrder.generateBill()
                println("\n=== SCENARIO 1: Food Order ===")
                println(foodBill.formatAsText())

                foodBill.formatAsText() shouldContain "Fish and Chips"
                foodBill.formatAsText() shouldContain "Grilled Salmon Salad"
                foodBill.formatAsText() shouldContain "Prawn Linguine"
                foodBill.formatAsText() shouldContain "Vegetable Thai Curry"
                foodBill.formatAsText() shouldContain "Halloumi Burger"
                foodBill.formatAsText() shouldContain "£86.00"
            }
        }

        And("the total spend for the group") {
            println("\n=== SCENARIO 1: Summary ===")
            println("Group of 6 pescatarians at Table 12")
            println("Drinks: £32.30")
            println("Food: £86.00")
            println("GRAND TOTAL: £118.30")
            println("Per person: £19.72")
        }
    }

    // ==================================================================================
    // SCENARIO 2: Couple wanting sharing selection and alcohol-free drinks
    // ==================================================================================
    Given("Scenario 2: A couple ordering sharing food and alcohol-free drinks") {
        val staffRepository = InMemoryStaffRepository()
        val menuRepository = InMemoryMenuRepository()
        val orderRepository = InMemoryOrderRepository()

        val waiter = StaffFixtures.barWorker(firstName = "James", lastName = "Bartender")
        staffRepository.save(waiter)

        // Create menu items with tracked IDs
        val virginMojitoId = MenuItemId.generate()
        val afIpaId = MenuItemId.generate()
        val elderflowerId = MenuItemId.generate()
        val orangeJuiceId = MenuItemId.generate()
        val medPlatterId = MenuItemId.generate()
        val seafoodPlatterId = MenuItemId.generate()
        val olivesId = MenuItemId.generate()
        val halloumiSticksId = MenuItemId.generate()

        val barMenu = MenuFixtures.barMenu(
            items = listOf(
                MenuFixtures.virginMojito(id = virginMojitoId),
                MenuFixtures.alcoholFreeIPA(id = afIpaId),
                MenuFixtures.sparklingElderflower(id = elderflowerId),
                MenuFixtures.freshOrangeJuice(id = orangeJuiceId),
                MenuFixtures.olives(id = olivesId),
                MenuFixtures.halloumiSticks(id = halloumiSticksId)
            )
        )

        val foodMenu = MenuFixtures.foodMenu(
            items = listOf(
                MenuFixtures.sharingPlatterMediterranean(id = medPlatterId),
                MenuFixtures.seafoodPlatter(id = seafoodPlatterId)
            )
        )

        menuRepository.save(barMenu)
        menuRepository.save(foodMenu)

        val orderService = OrderService(orderRepository, menuRepository, staffRepository)

        When("the couple orders alcohol-free drinks and sharing plates") {
            /*
             * Order for the couple:
             * DRINKS (alcohol-free):
             * - 2x Virgin Mojito @ £4.00 = £8.00
             * - 1x Alcohol-Free IPA @ £4.20 = £4.20
             * - 1x Sparkling Elderflower @ £3.50 = £3.50
             *
             * SHARING FOOD:
             * - 1x Mediterranean Sharing Platter @ £18.00 = £18.00
             * - 1x Seafood Sharing Platter @ £24.00 = £24.00
             * - 1x Marinated Olives @ £4.00 = £4.00
             * - 1x Halloumi Sticks @ £6.50 = £6.50
             *
             * Total: £68.20
             */
            val order = orderService.placeOrder(
                staffId = waiter.id.value,
                tableNumber = 7,
                items = listOf(
                    // Drinks
                    OrderService.OrderItemInput(virginMojitoId.value, 2),
                    OrderService.OrderItemInput(afIpaId.value, 1),
                    OrderService.OrderItemInput(elderflowerId.value, 1),
                    // Sharing food
                    OrderService.OrderItemInput(medPlatterId.value, 1),
                    OrderService.OrderItemInput(seafoodPlatterId.value, 1),
                    OrderService.OrderItemInput(olivesId.value, 1),
                    OrderService.OrderItemInput(halloumiSticksId.value, 1)
                )
            )

            Then("the order is created with all items and correct total") {
                order.items.size shouldBe 7
                order.totalAmount.amount shouldBe BigDecimal("68.20")

                val bill = order.generateBill()
                println("\n=== SCENARIO 2: Couple's Sharing Order ===")
                println(bill.formatAsText())

                bill.formatAsText() shouldContain "Table: 7"
                bill.formatAsText() shouldContain "Virgin Mojito"
                bill.formatAsText() shouldContain "Alcohol-Free IPA"
                bill.formatAsText() shouldContain "Mediterranean Sharing" // Name may be truncated
                bill.formatAsText() shouldContain "Seafood Sharing Platter"
                bill.formatAsText() shouldContain "£68.20"
            }
        }

        And("the couple's total") {
            println("\n=== SCENARIO 2: Summary ===")
            println("Couple at Table 7 (alcohol-free)")
            println("TOTAL: £68.20")
            println("Per person: £34.10")
        }
    }

    // ==================================================================================
    // SCENARIO 3: Large group of 20 wanting drinks and bar snacks
    // ==================================================================================
    Given("Scenario 3: A group of 20 people ordering 3 drinks each and bar snacks") {
        val staffRepository = InMemoryStaffRepository()
        val menuRepository = InMemoryMenuRepository()
        val orderRepository = InMemoryOrderRepository()

        val manager = StaffFixtures.manager(firstName = "Sophie", lastName = "Manager")
        staffRepository.save(manager)

        // Create menu items with tracked IDs
        val ginAndTonicId = MenuItemId.generate()
        val whiteWineId = MenuItemId.generate()
        val redWineId = MenuItemId.generate()
        val proseccoId = MenuItemId.generate()
        val craftBeerId = MenuItemId.generate()
        val mixedNutsId = MenuItemId.generate()
        val olivesId = MenuItemId.generate()
        val nachosId = MenuItemId.generate()
        val wingsId = MenuItemId.generate()
        val halloumiSticksId = MenuItemId.generate()

        val barMenu = MenuFixtures.barMenu(
            items = listOf(
                MenuFixtures.ginAndTonic(id = ginAndTonicId),
                MenuFixtures.houseWineWhite(id = whiteWineId),
                MenuFixtures.houseWineRed(id = redWineId),
                MenuFixtures.proseccoGlass(id = proseccoId),
                MenuFixtures.craftBeerPint(id = craftBeerId),
                MenuFixtures.mixedNuts(id = mixedNutsId),
                MenuFixtures.olives(id = olivesId),
                MenuFixtures.nachos(id = nachosId),
                MenuFixtures.chickenWings(id = wingsId),
                MenuFixtures.halloumiSticks(id = halloumiSticksId)
            )
        )

        menuRepository.save(barMenu)

        val orderService = OrderService(orderRepository, menuRepository, staffRepository)

        When("the group orders their first round of drinks") {
            /*
             * First round for 20 people:
             * - 6x Gin & Tonic @ £4.50 = £27.00
             * - 4x House White Wine @ £5.50 = £22.00
             * - 3x House Red Wine @ £5.50 = £16.50
             * - 4x Prosecco @ £6.50 = £26.00
             * - 3x Craft IPA Pint @ £5.80 = £17.40
             * Total: £108.90
             */
            val round1 = orderService.placeOrder(
                staffId = manager.id.value,
                tableNumber = 1, // Large party - reserved area
                items = listOf(
                    OrderService.OrderItemInput(ginAndTonicId.value, 6),
                    OrderService.OrderItemInput(whiteWineId.value, 4),
                    OrderService.OrderItemInput(redWineId.value, 3),
                    OrderService.OrderItemInput(proseccoId.value, 4),
                    OrderService.OrderItemInput(craftBeerId.value, 3)
                )
            )

            Then("round 1 order is correct") {
                round1.totalAmount.amount shouldBe BigDecimal("108.90")

                val bill = round1.generateBill()
                println("\n=== SCENARIO 3: Round 1 ===")
                println(bill.formatAsText())
            }
        }

        When("the group orders their second round with some bar snacks") {
            /*
             * Second round for 20 people (same drinks pattern) + snacks:
             * DRINKS:
             * - 6x Gin & Tonic @ £4.50 = £27.00
             * - 4x House White Wine @ £5.50 = £22.00
             * - 3x House Red Wine @ £5.50 = £16.50
             * - 4x Prosecco @ £6.50 = £26.00
             * - 3x Craft IPA Pint @ £5.80 = £17.40
             *
             * BAR SNACKS:
             * - 4x Mixed Nuts @ £3.50 = £14.00
             * - 3x Marinated Olives @ £4.00 = £12.00
             * - 4x Loaded Nachos @ £7.50 = £30.00
             * - 3x Buffalo Wings @ £8.00 = £24.00
             * - 2x Halloumi Sticks @ £6.50 = £13.00
             *
             * Total: £201.90
             */
            val round2 = orderService.placeOrder(
                staffId = manager.id.value,
                tableNumber = 1,
                items = listOf(
                    // Drinks
                    OrderService.OrderItemInput(ginAndTonicId.value, 6),
                    OrderService.OrderItemInput(whiteWineId.value, 4),
                    OrderService.OrderItemInput(redWineId.value, 3),
                    OrderService.OrderItemInput(proseccoId.value, 4),
                    OrderService.OrderItemInput(craftBeerId.value, 3),
                    // Bar snacks
                    OrderService.OrderItemInput(mixedNutsId.value, 4),
                    OrderService.OrderItemInput(olivesId.value, 3),
                    OrderService.OrderItemInput(nachosId.value, 4),
                    OrderService.OrderItemInput(wingsId.value, 3),
                    OrderService.OrderItemInput(halloumiSticksId.value, 2)
                )
            )

            Then("round 2 order with snacks is correct") {
                round2.totalAmount.amount shouldBe BigDecimal("201.90")

                val bill = round2.generateBill()
                println("\n=== SCENARIO 3: Round 2 (with bar snacks) ===")
                println(bill.formatAsText())

                bill.formatAsText() shouldContain "Mixed Nuts"
                bill.formatAsText() shouldContain "Loaded Nachos"
                bill.formatAsText() shouldContain "Buffalo Wings"
            }
        }

        When("the group orders their third round of drinks") {
            /*
             * Third round for 20 people:
             * - 6x Gin & Tonic @ £4.50 = £27.00
             * - 4x House White Wine @ £5.50 = £22.00
             * - 3x House Red Wine @ £5.50 = £16.50
             * - 4x Prosecco @ £6.50 = £26.00
             * - 3x Craft IPA Pint @ £5.80 = £17.40
             * Total: £108.90
             */
            val round3 = orderService.placeOrder(
                staffId = manager.id.value,
                tableNumber = 1,
                items = listOf(
                    OrderService.OrderItemInput(ginAndTonicId.value, 6),
                    OrderService.OrderItemInput(whiteWineId.value, 4),
                    OrderService.OrderItemInput(redWineId.value, 3),
                    OrderService.OrderItemInput(proseccoId.value, 4),
                    OrderService.OrderItemInput(craftBeerId.value, 3)
                )
            )

            Then("round 3 order is correct") {
                round3.totalAmount.amount shouldBe BigDecimal("108.90")

                val bill = round3.generateBill()
                println("\n=== SCENARIO 3: Round 3 ===")
                println(bill.formatAsText())
            }
        }

        And("the total spend for the large group") {
            println("\n=== SCENARIO 3: Summary ===")
            println("Group of 20 at Table 1 (Reserved Area)")
            println("Round 1 (drinks): £108.90")
            println("Round 2 (drinks + snacks): £201.90")
            println("Round 3 (drinks): £108.90")
            println("GRAND TOTAL: £419.70")
            println("Per person: £20.99")
            println("Average drinks per person: 3 (as requested)")
        }
    }

    // ==================================================================================
    // Summary report showing all three scenarios
    // ==================================================================================
    afterSpec {
        println("\n")
        println("=" .repeat(60))
        println("           SCENARIO TEST SUMMARY")
        println("=" .repeat(60))
        println()
        println("Scenario 1: Group of 6 Pescatarians")
        println("  - Drinks (G&Ts, wine, prosecco, beer): £32.30")
        println("  - Food (fish, salmon, prawns, veggie): £86.00")
        println("  - Total: £118.30 (£19.72/person)")
        println()
        println("Scenario 2: Couple - Sharing & Alcohol-Free")
        println("  - Drinks (mojitos, AF beer, elderflower): £15.70")
        println("  - Sharing plates (Mediterranean, Seafood): £52.50")
        println("  - Total: £68.20 (£34.10/person)")
        println()
        println("Scenario 3: Group of 20 - Drinks & Snacks")
        println("  - 3 rounds of drinks (60 drinks total): £326.70")
        println("  - Bar snacks (nuts, olives, nachos, wings): £93.00")
        println("  - Total: £419.70 (£20.99/person)")
        println()
        println("=" .repeat(60))
    }
})
