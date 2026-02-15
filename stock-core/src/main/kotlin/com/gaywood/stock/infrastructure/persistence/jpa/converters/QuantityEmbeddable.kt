package com.gaywood.stock.infrastructure.persistence.jpa.converters

import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.Unit
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.math.BigDecimal

@Embeddable
class QuantityEmbeddable(
    @Column(name = "quantity_amount", precision = 19, scale = 4)
    var amount: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_unit", length = 20)
    var unit: Unit = Unit.PIECES
) {
    fun toDomain(): Quantity = Quantity(amount, unit)

    companion object {
        fun from(quantity: Quantity): QuantityEmbeddable =
            QuantityEmbeddable(quantity.amount, quantity.unit)

        fun fromNullable(quantity: Quantity?): QuantityEmbeddable? =
            quantity?.let { from(it) }
    }
}
