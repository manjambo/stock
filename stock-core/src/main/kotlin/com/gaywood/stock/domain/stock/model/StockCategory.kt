package com.gaywood.stock.domain.stock.model

sealed class StockCategory(val displayName: String, val location: StockLocation) {

    sealed class Bar(displayName: String) : StockCategory(displayName, StockLocation.BAR) {
        data object Spirits : Bar("Spirits")
        data object Wine : Bar("Wine")
        data object Beer : Bar("Beer")
        data object Mixers : Bar("Mixers")
        data object Garnishes : Bar("Garnishes")
    }

    sealed class Kitchen(displayName: String) : StockCategory(displayName, StockLocation.KITCHEN) {
        data object Proteins : Kitchen("Proteins")
        data object Vegetables : Kitchen("Vegetables")
        data object Dairy : Kitchen("Dairy")
        data object DryGoods : Kitchen("Dry Goods")
        data object Spices : Kitchen("Spices")
        data object Frozen : Kitchen("Frozen")
    }

    override fun toString(): String = displayName
}
