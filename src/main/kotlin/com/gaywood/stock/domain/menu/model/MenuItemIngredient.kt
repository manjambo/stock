package com.gaywood.stock.domain.menu.model

import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockItemId

data class MenuItemIngredient(
    val stockItemId: StockItemId,
    val quantityPerServing: Quantity
) {
    init {
        require(!quantityPerServing.isZero()) { "Quantity per serving must be greater than zero" }
    }
}
