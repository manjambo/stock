package com.gaywood.stock.domain.menu.model

import com.gaywood.stock.domain.shared.AggregateRoot
import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.model.StockItemId

class Menu private constructor(
    override val id: MenuId,
    private var _name: String,
    private var _description: String,
    val type: MenuType,
    private val _items: MutableMap<MenuItemId, MenuItem>,
    private var _active: Boolean
) : AggregateRoot<MenuId>() {

    val name: String get() = _name
    val description: String get() = _description
    val items: List<MenuItem> get() = _items.values.toList()
    val active: Boolean get() = _active

    fun updateDetails(name: String, description: String) {
        require(name.isNotBlank()) { "Menu name cannot be blank" }
        _name = name
        _description = description
    }

    fun addItem(item: MenuItem) {
        _items[item.id] = item
    }

    fun removeItem(itemId: MenuItemId) {
        _items.remove(itemId)
    }

    fun getItem(itemId: MenuItemId): MenuItem? = _items[itemId]

    fun findItemByName(name: String): MenuItem? =
        _items.values.find { it.name.equals(name, ignoreCase = true) }

    fun activate() {
        _active = true
    }

    fun deactivate() {
        _active = false
    }

    fun availableItems(): List<MenuItem> = _items.values.filter { it.available }

    /**
     * Returns all allergens present in any menu item.
     * Uses cached allergens from menu items - call [refreshAllAllergens] if stock item allergens have changed.
     */
    fun collectAllAllergens(): Set<Allergen> =
        _items.values.flatMap { it.allergens }.toSet()

    /**
     * Finds menu items containing the specified allergen.
     * Uses cached allergens from menu items.
     */
    fun findItemsContainingAllergen(allergen: Allergen): List<MenuItem> =
        _items.values.filter { allergen in it.allergens }

    /**
     * Finds menu items that don't contain any of the specified allergens.
     * Uses cached allergens from menu items.
     */
    fun findItemsFreeOfAllergens(allergens: Set<Allergen>): List<MenuItem> =
        _items.values.filter { item -> item.allergens.none { it in allergens } }

    /**
     * Refreshes cached allergens for all menu items from the provided stock items.
     * Call this when stock item allergens change or after loading menus from persistence.
     */
    fun refreshAllAllergens(stockItemsById: Map<StockItemId, StockItem>) {
        _items.values.forEach { it.refreshAllergens(stockItemsById) }
    }

    companion object {
        fun create(
            id: MenuId = MenuId.generate(),
            name: String,
            description: String = "",
            type: MenuType,
            items: List<MenuItem> = emptyList(),
            active: Boolean = true
        ): Menu {
            require(name.isNotBlank()) { "Menu name cannot be blank" }
            return Menu(
                id = id,
                _name = name,
                _description = description,
                type = type,
                _items = items.associateBy { it.id }.toMutableMap(),
                _active = active
            )
        }
    }
}
