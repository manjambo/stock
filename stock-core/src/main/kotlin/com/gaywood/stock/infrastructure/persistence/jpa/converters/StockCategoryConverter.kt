package com.gaywood.stock.infrastructure.persistence.jpa.converters

import com.gaywood.stock.domain.stock.model.StockCategory
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class StockCategoryConverter : AttributeConverter<StockCategory, String> {

    override fun convertToDatabaseColumn(attribute: StockCategory?): String? {
        return attribute?.let { toDbValue(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): StockCategory? {
        return dbData?.let { fromDbValue(it) }
    }

    companion object {
        fun toDbValue(category: StockCategory): String = when (category) {
            is StockCategory.Bar.Spirits -> "BAR_SPIRITS"
            is StockCategory.Bar.Wine -> "BAR_WINE"
            is StockCategory.Bar.Beer -> "BAR_BEER"
            is StockCategory.Bar.Mixers -> "BAR_MIXERS"
            is StockCategory.Bar.Garnishes -> "BAR_GARNISHES"
            is StockCategory.Kitchen.Proteins -> "KITCHEN_PROTEINS"
            is StockCategory.Kitchen.Vegetables -> "KITCHEN_VEGETABLES"
            is StockCategory.Kitchen.Dairy -> "KITCHEN_DAIRY"
            is StockCategory.Kitchen.DryGoods -> "KITCHEN_DRY_GOODS"
            is StockCategory.Kitchen.Spices -> "KITCHEN_SPICES"
            is StockCategory.Kitchen.Frozen -> "KITCHEN_FROZEN"
        }

        fun fromDbValue(value: String): StockCategory = when (value) {
            "BAR_SPIRITS" -> StockCategory.Bar.Spirits
            "BAR_WINE" -> StockCategory.Bar.Wine
            "BAR_BEER" -> StockCategory.Bar.Beer
            "BAR_MIXERS" -> StockCategory.Bar.Mixers
            "BAR_GARNISHES" -> StockCategory.Bar.Garnishes
            "KITCHEN_PROTEINS" -> StockCategory.Kitchen.Proteins
            "KITCHEN_VEGETABLES" -> StockCategory.Kitchen.Vegetables
            "KITCHEN_DAIRY" -> StockCategory.Kitchen.Dairy
            "KITCHEN_DRY_GOODS" -> StockCategory.Kitchen.DryGoods
            "KITCHEN_SPICES" -> StockCategory.Kitchen.Spices
            "KITCHEN_FROZEN" -> StockCategory.Kitchen.Frozen
            else -> throw IllegalArgumentException("Unknown stock category: $value")
        }
    }
}
