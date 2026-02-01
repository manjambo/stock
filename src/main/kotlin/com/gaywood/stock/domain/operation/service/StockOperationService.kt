package com.gaywood.stock.domain.operation.service

import com.gaywood.stock.domain.shared.LocationAccessDeniedException
import com.gaywood.stock.domain.shared.PermissionDeniedException
import com.gaywood.stock.domain.shared.StockItemNotFoundException
import com.gaywood.stock.domain.staff.model.Permission
import com.gaywood.stock.domain.staff.model.Staff
import com.gaywood.stock.domain.stock.model.LowStockThreshold
import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.model.StockItemId
import com.gaywood.stock.domain.stock.model.StockLocation
import com.gaywood.stock.domain.stock.repository.StockRepository

class StockOperationService(
    private val stockRepository: StockRepository
) {

    fun viewStock(staff: Staff, location: StockLocation): List<StockItem> {
        requirePermission(staff, Permission.VIEW_STOCK)
        requireLocationAccess(staff, location)
        return stockRepository.findByLocation(location)
    }

    fun viewAllStock(staff: Staff): List<StockItem> {
        requirePermission(staff, Permission.VIEW_STOCK)
        return stockRepository.findAll().filter { staff.canAccessLocation(it.location) }
    }

    fun addStock(staff: Staff, stockItemId: StockItemId, quantity: Quantity): StockItem {
        requirePermission(staff, Permission.ADD_STOCK)
        val stockItem = findStockItemOrThrow(stockItemId)
        requireLocationAccess(staff, stockItem.location)

        stockItem.addStock(quantity)
        return stockRepository.save(stockItem)
    }

    fun removeStock(staff: Staff, stockItemId: StockItemId, quantity: Quantity): StockItem {
        requirePermission(staff, Permission.REMOVE_STOCK)
        val stockItem = findStockItemOrThrow(stockItemId)
        requireLocationAccess(staff, stockItem.location)

        stockItem.removeStock(quantity)
        return stockRepository.save(stockItem)
    }

    fun adjustStock(staff: Staff, stockItemId: StockItemId, newQuantity: Quantity, reason: String): StockItem {
        requirePermission(staff, Permission.ADJUST_STOCK)
        val stockItem = findStockItemOrThrow(stockItemId)
        requireLocationAccess(staff, stockItem.location)

        stockItem.adjustStock(newQuantity, reason)
        return stockRepository.save(stockItem)
    }

    fun setLowStockThreshold(staff: Staff, stockItemId: StockItemId, threshold: LowStockThreshold): StockItem {
        requirePermission(staff, Permission.SET_THRESHOLDS)
        val stockItem = findStockItemOrThrow(stockItemId)
        requireLocationAccess(staff, stockItem.location)

        stockItem.setLowStockThreshold(threshold)
        return stockRepository.save(stockItem)
    }

    fun viewLowStockItems(staff: Staff): List<StockItem> {
        requirePermission(staff, Permission.VIEW_STOCK)
        return stockRepository.findLowStockItems().filter { staff.canAccessLocation(it.location) }
    }

    private fun findStockItemOrThrow(id: StockItemId): StockItem {
        return stockRepository.findById(id)
            ?: throw StockItemNotFoundException(id.value)
    }

    private fun requirePermission(staff: Staff, permission: Permission) {
        staff.takeUnless { it.hasPermission(permission) }?.let {
            throw PermissionDeniedException(permission.name, it.role.name)
        }
    }

    private fun requireLocationAccess(staff: Staff, location: StockLocation) {
        staff.takeUnless { it.canAccessLocation(location) }?.let {
            throw LocationAccessDeniedException(it.name.fullName, location.name)
        }
    }
}
