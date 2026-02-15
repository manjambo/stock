package com.gaywood.stock.domain.menu.model

import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockItemId
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class MenuItemIngredient(
    @field:NotNull(message = "Stock item ID cannot be null")
    val stockItemId: StockItemId,

    @field:NotNull(message = "Quantity per serving cannot be null")
    @field:Valid
    val quantityPerServing: Quantity
) {
    init {
        require(!quantityPerServing.isZero()) { "Quantity per serving must be greater than zero" }
    }
}
