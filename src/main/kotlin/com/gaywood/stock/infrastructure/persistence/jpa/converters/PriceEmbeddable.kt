package com.gaywood.stock.infrastructure.persistence.jpa.converters

import com.gaywood.stock.domain.menu.model.Price
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal
import java.util.Currency

@Embeddable
class PriceEmbeddable(
    @Column(name = "price_amount", precision = 19, scale = 4)
    var amount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "price_currency", length = 3)
    var currency: String = "GBP"
) {
    fun toDomain(): Price = Price(amount, Currency.getInstance(currency))

    companion object {
        fun from(price: Price): PriceEmbeddable =
            PriceEmbeddable(price.amount, price.currency.currencyCode)
    }
}
