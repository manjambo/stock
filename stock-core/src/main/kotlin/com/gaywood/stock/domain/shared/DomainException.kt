package com.gaywood.stock.domain.shared

open class DomainException(message: String) : RuntimeException(message)

class InsufficientStockException(itemName: String, requested: String, available: String) :
    DomainException("Insufficient stock for '$itemName': requested $requested but only $available available")

class InvalidQuantityException(message: String) : DomainException(message)

class PermissionDeniedException(permission: String, role: String) :
    DomainException("Permission '$permission' denied for role '$role'")

class StockItemNotFoundException(id: String) :
    DomainException("Stock item not found: $id")

class StaffNotFoundException(id: String) :
    DomainException("Staff not found: $id")

class OrderNotFoundException(id: String) :
    DomainException("Order not found: $id")

class MenuItemNotFoundException(id: String) :
    DomainException("Menu item not found: $id")

class LocationAccessDeniedException(staffName: String, location: String) :
    DomainException("Staff '$staffName' does not have access to location '$location'")
