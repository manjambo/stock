package com.gaywood.stock.domain.menu.model

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

data class Price(
    @field:NotNull(message = "Amount cannot be null")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    @field:Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    val amount: BigDecimal,

    @field:NotNull(message = "Currency cannot be null")
    val currency: Currency = Currency.getInstance("GBP")
) : Comparable<Price> {

    init {
        require(amount >= BigDecimal.ZERO) { "Price cannot be negative: $amount" }
    }

    constructor(amount: Double, currency: Currency = Currency.getInstance("GBP"))
        : this(BigDecimal.valueOf(amount), currency)

    constructor(amount: Int, currency: Currency = Currency.getInstance("GBP"))
        : this(BigDecimal(amount), currency)

    override fun compareTo(other: Price): Int {
        require(currency == other.currency) { "Cannot compare prices with different currencies" }
        return amount.compareTo(other.amount)
    }

    operator fun plus(other: Price): Price {
        require(currency == other.currency) { "Cannot add prices with different currencies" }
        return Price(amount + other.amount, currency)
    }

    operator fun times(multiplier: Int): Price {
        return Price(amount * BigDecimal(multiplier), currency)
    }

    override fun toString(): String {
        val symbol = currency.symbol
        return "$symbol${amount.setScale(2, RoundingMode.HALF_UP)}"
    }

    companion object {
        fun zero(currency: Currency = Currency.getInstance("GBP")): Price =
            Price(BigDecimal.ZERO, currency)
    }
}
