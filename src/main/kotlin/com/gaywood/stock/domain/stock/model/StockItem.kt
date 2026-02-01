package com.gaywood.stock.domain.stock.model

import com.gaywood.stock.domain.shared.AggregateRoot
import com.gaywood.stock.domain.shared.InsufficientStockException
import com.gaywood.stock.domain.stock.event.StockEvent

class StockItem private constructor(
    override val id: StockItemId,
    val name: String,
    val category: StockCategory,
    private var _quantity: Quantity,
    private var _lowStockThreshold: LowStockThreshold?,
    private val _allergens: MutableSet<Allergen>
) : AggregateRoot<StockItemId>() {

    val quantity: Quantity
        get() = _quantity

    val lowStockThreshold: LowStockThreshold?
        get() = _lowStockThreshold

    val allergens: Set<Allergen>
        get() = _allergens.toSet()

    val location: StockLocation
        get() = category.location

    fun containsAllergen(allergen: Allergen): Boolean = allergen in _allergens

    fun addAllergen(allergen: Allergen) {
        _allergens.add(allergen).takeIf { it }?.let {
            registerEvent(
                StockEvent.AllergensUpdated(
                    stockItemId = id,
                    itemName = name,
                    allergens = allergens
                )
            )
        }
    }

    fun removeAllergen(allergen: Allergen) {
        _allergens.remove(allergen).takeIf { it }?.let {
            registerEvent(
                StockEvent.AllergensUpdated(
                    stockItemId = id,
                    itemName = name,
                    allergens = allergens
                )
            )
        }
    }

    fun updateAllergens(newAllergens: Set<Allergen>) {
        newAllergens.takeUnless { it == _allergens }?.let {
            _allergens.clear()
            _allergens.addAll(it)
            registerEvent(
                StockEvent.AllergensUpdated(
                    stockItemId = id,
                    itemName = name,
                    allergens = allergens
                )
            )
        }
    }

    fun addStock(amount: Quantity) {
        require(amount.unit == _quantity.unit) {
            "Cannot add stock with different unit: ${amount.unit} vs ${_quantity.unit}"
        }
        _quantity = _quantity + amount
        registerEvent(
            StockEvent.StockAdded(
                stockItemId = id,
                quantityAdded = amount,
                newTotal = _quantity
            )
        )
    }

    fun removeStock(amount: Quantity) {
        require(amount.unit == _quantity.unit) {
            "Cannot remove stock with different unit: ${amount.unit} vs ${_quantity.unit}"
        }
        amount.takeIf { it.isGreaterThan(_quantity) }?.let {
            throw InsufficientStockException(name, amount.toString(), _quantity.toString())
        }
        _quantity = _quantity - amount
        registerEvent(
            StockEvent.StockRemoved(
                stockItemId = id,
                quantityRemoved = amount,
                newTotal = _quantity
            )
        )
        checkLowStockThreshold()
    }

    fun adjustStock(newQuantity: Quantity, reason: String) {
        require(newQuantity.unit == _quantity.unit) {
            "Cannot adjust stock with different unit: ${newQuantity.unit} vs ${_quantity.unit}"
        }
        val previousQuantity = _quantity
        _quantity = newQuantity
        registerEvent(
            StockEvent.StockAdjusted(
                stockItemId = id,
                previousQuantity = previousQuantity,
                newQuantity = newQuantity,
                reason = reason
            )
        )
        checkLowStockThreshold()
    }

    fun setLowStockThreshold(threshold: LowStockThreshold) {
        require(threshold.quantity.unit == _quantity.unit) {
            "Threshold unit must match stock unit: ${threshold.quantity.unit} vs ${_quantity.unit}"
        }
        val previousThreshold = _lowStockThreshold
        _lowStockThreshold = threshold
        registerEvent(
            StockEvent.ThresholdUpdated(
                stockItemId = id,
                previousThreshold = previousThreshold?.quantity,
                newThreshold = threshold.quantity
            )
        )
        checkLowStockThreshold()
    }

    fun isLowStock(): Boolean {
        return _lowStockThreshold?.isBreached(_quantity) ?: false
    }

    private fun checkLowStockThreshold() {
        _lowStockThreshold?.takeIf { it.isBreached(_quantity) }?.let { threshold ->
            registerEvent(
                StockEvent.LowStockAlertRaised(
                    stockItemId = id,
                    itemName = name,
                    currentQuantity = _quantity,
                    threshold = threshold.quantity
                )
            )
        }
    }

    companion object {
        fun create(
            id: StockItemId = StockItemId.generate(),
            name: String,
            category: StockCategory,
            initialQuantity: Quantity,
            lowStockThreshold: LowStockThreshold? = null,
            allergens: Set<Allergen> = emptySet()
        ): StockItem {
            require(name.isNotBlank()) { "Stock item name cannot be blank" }
            lowStockThreshold?.let {
                require(it.quantity.unit == initialQuantity.unit) {
                    "Threshold unit must match stock unit"
                }
            }
            return StockItem(
                id = id,
                name = name,
                category = category,
                _quantity = initialQuantity,
                _lowStockThreshold = lowStockThreshold,
                _allergens = allergens.toMutableSet()
            )
        }
    }
}
