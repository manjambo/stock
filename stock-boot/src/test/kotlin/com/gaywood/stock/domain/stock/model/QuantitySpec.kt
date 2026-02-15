package com.gaywood.stock.domain.stock.model

import com.gaywood.stock.domain.shared.InvalidQuantityException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.math.BigDecimal

class QuantitySpec : BehaviorSpec({

    Given("a quantity with positive amount") {
        val quantity = Quantity(10, Unit.BOTTLES)

        Then("it should be created successfully") {
            quantity.amount shouldBe BigDecimal(10)
            quantity.unit shouldBe Unit.BOTTLES
        }

        Then("toString should format correctly") {
            quantity.toString() shouldContain "10"
            quantity.toString() shouldContain "bottles"
        }
    }

    Given("a quantity with zero amount") {
        val quantity = Quantity(0, Unit.KILOGRAMS)

        Then("it should be created successfully") {
            quantity.amount shouldBe BigDecimal.ZERO
            quantity.isZero() shouldBe true
        }
    }

    Given("attempting to create a quantity with negative amount") {
        Then("it should throw InvalidQuantityException") {
            shouldThrow<InvalidQuantityException> {
                Quantity(-5, Unit.BOTTLES)
            }
        }
    }

    Given("two quantities with the same unit") {
        val q1 = Quantity(10, Unit.BOTTLES)
        val q2 = Quantity(5, Unit.BOTTLES)

        When("adding them together") {
            val result = q1 + q2

            Then("the amounts should be summed") {
                result.amount shouldBe BigDecimal(15)
                result.unit shouldBe Unit.BOTTLES
            }
        }

        When("subtracting one from another") {
            val result = q1 - q2

            Then("the amounts should be subtracted") {
                result.amount shouldBe BigDecimal(5)
                result.unit shouldBe Unit.BOTTLES
            }
        }

        When("comparing them") {
            Then("greater than should work correctly") {
                q1.isGreaterThan(q2) shouldBe true
                q2.isGreaterThan(q1) shouldBe false
            }

            Then("less than should work correctly") {
                q2.isLessThan(q1) shouldBe true
                q1.isLessThan(q2) shouldBe false
            }

            Then("compareTo should work correctly") {
                (q1 > q2) shouldBe true
                (q2 < q1) shouldBe true
            }
        }
    }

    Given("subtracting a larger quantity from a smaller one") {
        val smaller = Quantity(5, Unit.BOTTLES)
        val larger = Quantity(10, Unit.BOTTLES)

        Then("it should throw InvalidQuantityException") {
            shouldThrow<InvalidQuantityException> {
                smaller - larger
            }
        }
    }

    Given("two quantities with different units") {
        val bottles = Quantity(10, Unit.BOTTLES)
        val kilograms = Quantity(5, Unit.KILOGRAMS)

        Then("adding should throw InvalidQuantityException") {
            shouldThrow<InvalidQuantityException> {
                bottles + kilograms
            }
        }

        Then("subtracting should throw InvalidQuantityException") {
            shouldThrow<InvalidQuantityException> {
                bottles - kilograms
            }
        }

        Then("comparing should throw InvalidQuantityException") {
            shouldThrow<InvalidQuantityException> {
                bottles.isLessThan(kilograms)
            }
        }
    }

    Given("a zero quantity") {
        val zero = Quantity.zero(Unit.LITERS)

        Then("isZero should return true") {
            zero.isZero() shouldBe true
        }

        Then("it should have the correct unit") {
            zero.unit shouldBe Unit.LITERS
        }
    }

    Given("quantities with decimal values") {
        val q1 = Quantity(10.5, Unit.KILOGRAMS)
        val q2 = Quantity(2.3, Unit.KILOGRAMS)

        When("performing arithmetic") {
            val sum = q1 + q2
            val difference = q1 - q2

            Then("sum should be correct") {
                sum.amount.toDouble() shouldBe 12.8
            }

            Then("difference should be correct") {
                difference.amount.toDouble() shouldBe 8.2
            }
        }
    }
})
