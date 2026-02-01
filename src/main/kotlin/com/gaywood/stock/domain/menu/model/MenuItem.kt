package com.gaywood.stock.domain.menu.model

import com.gaywood.stock.domain.shared.Entity
import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.StockItem

class MenuItem private constructor(
    override val id: MenuItemId,
    private var _name: String,
    private var _description: String,
    private var _price: Price,
    private val _ingredients: MutableList<MenuItemIngredient>,
    private var _available: Boolean
) : Entity<MenuItemId> {

    val name: String get() = _name
    val description: String get() = _description
    val price: Price get() = _price
    val ingredients: List<MenuItemIngredient> get() = _ingredients.toList()
    val available: Boolean get() = _available

    fun updateDetails(name: String, description: String, price: Price) {
        require(name.isNotBlank()) { "Menu item name cannot be blank" }
        _name = name
        _description = description
        _price = price
    }

    fun addIngredient(ingredient: MenuItemIngredient) {
        replaceExistingOrAdd(ingredient)
    }

    private fun replaceExistingOrAdd(ingredient: MenuItemIngredient) {
        val existingIndex = findIngredientIndexByStockItem(ingredient.stockItemId)
        when {
            existingIndex >= 0 -> _ingredients[existingIndex] = ingredient
            else -> _ingredients.add(ingredient)
        }
    }

    private fun findIngredientIndexByStockItem(stockItemId: com.gaywood.stock.domain.stock.model.StockItemId) =
        _ingredients.indexOfFirst { it.stockItemId == stockItemId }

    fun removeIngredient(stockItemId: com.gaywood.stock.domain.stock.model.StockItemId) {
        _ingredients.removeIf { it.stockItemId == stockItemId }
    }

    fun clearIngredients() {
        _ingredients.clear()
    }

    fun setAvailable(available: Boolean) {
        _available = available
    }

    fun collectAllergensFromIngredients(stockItemsById: Map<com.gaywood.stock.domain.stock.model.StockItemId, StockItem>): Set<Allergen> {
        return _ingredients
            .mapNotNull { stockItemsById[it.stockItemId] }
            .flatMap { it.allergens }
            .toSet()
    }

    companion object {
        fun create(
            id: MenuItemId = MenuItemId.generate(),
            name: String,
            description: String = "",
            price: Price,
            ingredients: List<MenuItemIngredient> = emptyList(),
            available: Boolean = true
        ): MenuItem {
            require(name.isNotBlank()) { "Menu item name cannot be blank" }
            return MenuItem(
                id = id,
                _name = name,
                _description = description,
                _price = price,
                _ingredients = ingredients.toMutableList(),
                _available = available
            )
        }
    }
}
