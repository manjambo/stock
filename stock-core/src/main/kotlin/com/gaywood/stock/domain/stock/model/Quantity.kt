package com.gaywood.stock.domain.stock.model

import com.gaywood.stock.domain.shared.InvalidQuantityException
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.math.RoundingMode

data class Quantity(
    @field:NotNull(message = "Amount cannot be null")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "Amount cannot be negative")
    @field:Digits(integer = 10, fraction = 3, message = "Amount must have at most 10 integer digits and 3 decimal places")
    val amount: BigDecimal,

    @field:NotNull(message = "Unit cannot be null")
    val unit: Unit
) : Comparable<Quantity> {

    init {
        amount.takeIf { it < BigDecimal.ZERO }?.let {
            throw InvalidQuantityException("Quantity amount cannot be negative: $amount")
        }
    }

    constructor(amount: Int, unit: Unit) : this(BigDecimal(amount), unit)
    constructor(amount: Double, unit: Unit) : this(BigDecimal.valueOf(amount), unit)

    operator fun plus(other: Quantity): Quantity {
        requireSameUnit(other)
        return Quantity(amount.add(other.amount), unit)
    }

    operator fun minus(other: Quantity): Quantity {
        requireSameUnit(other)
        val result = amount.subtract(other.amount)
        result.takeIf { it < BigDecimal.ZERO }?.let {
            throw InvalidQuantityException("Cannot subtract $other from $this: would result in negative quantity")
        }
        return Quantity(result, unit)
    }

    fun isZero(): Boolean = amount.compareTo(BigDecimal.ZERO) == 0

    fun isLessThan(other: Quantity): Boolean {
        requireSameUnit(other)
        return amount < other.amount
    }

    fun isLessThanOrEqualTo(other: Quantity): Boolean {
        requireSameUnit(other)
        return amount <= other.amount
    }

    fun isGreaterThan(other: Quantity): Boolean {
        requireSameUnit(other)
        return amount > other.amount
    }

    private fun requireSameUnit(other: Quantity) {
        other.takeUnless { it.unit == unit }?.let {
            throw InvalidQuantityException("Cannot operate on quantities with different units: $unit vs ${other.unit}")
        }
    }

    override fun compareTo(other: Quantity): Int {
        requireSameUnit(other)
        return amount.compareTo(other.amount)
    }

    override fun toString(): String = "${amount.setScale(2, RoundingMode.HALF_UP)} ${unit.displayName}"

    companion object {
        fun zero(unit: Unit): Quantity = Quantity(BigDecimal.ZERO, unit)
    }
}
