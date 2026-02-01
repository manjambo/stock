package com.gaywood.stock.domain.menu.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

data class Price(
    val amount: BigDecimal,
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
