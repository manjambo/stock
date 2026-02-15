package com.gaywood.stock.api.advice

import com.gaywood.stock.api.dto.ErrorResponse
import com.gaywood.stock.domain.shared.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global exception handler that maps exceptions to consistent HTTP responses.
 *
 * Error code naming convention:
 * - Resource-specific: {RESOURCE}_{ERROR_TYPE} (e.g., ORDER_NOT_FOUND, STOCK_INSUFFICIENT)
 * - Generic: {ERROR_TYPE} (e.g., VALIDATION_ERROR, INTERNAL_ERROR)
 *
 * HTTP Status mapping:
 * - 400 Bad Request: Invalid input, validation errors, business rule violations
 * - 403 Forbidden: Permission/access denied
 * - 404 Not Found: Resource not found
 * - 409 Conflict: State conflicts (e.g., invalid state transitions)
 * - 500 Internal Server Error: Unexpected errors
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    // ===========================================
    // 404 Not Found - Resource not found
    // ===========================================

    @ExceptionHandler(OrderNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleOrderNotFound(ex: OrderNotFoundException) = ErrorResponse(
        error = "ORDER_NOT_FOUND",
        message = ex.message ?: "Order not found"
    )

    @ExceptionHandler(StaffNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleStaffNotFound(ex: StaffNotFoundException) = ErrorResponse(
        error = "STAFF_NOT_FOUND",
        message = ex.message ?: "Staff not found"
    )

    @ExceptionHandler(MenuItemNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleMenuItemNotFound(ex: MenuItemNotFoundException) = ErrorResponse(
        error = "MENU_ITEM_NOT_FOUND",
        message = ex.message ?: "Menu item not found"
    )

    @ExceptionHandler(StockItemNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleStockItemNotFound(ex: StockItemNotFoundException) = ErrorResponse(
        error = "STOCK_ITEM_NOT_FOUND",
        message = ex.message ?: "Stock item not found"
    )

    // ===========================================
    // 403 Forbidden - Access denied
    // ===========================================

    @ExceptionHandler(PermissionDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handlePermissionDenied(ex: PermissionDeniedException) = ErrorResponse(
        error = "PERMISSION_DENIED",
        message = "Access denied"
    )

    @ExceptionHandler(LocationAccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleLocationAccessDenied(ex: LocationAccessDeniedException) = ErrorResponse(
        error = "LOCATION_ACCESS_DENIED",
        message = "Access denied"
    )

    // ===========================================
    // 400 Bad Request - Invalid input / Business rule violations
    // ===========================================

    @ExceptionHandler(InsufficientStockException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInsufficientStock(ex: InsufficientStockException) = ErrorResponse(
        error = "STOCK_INSUFFICIENT",
        message = ex.message ?: "Insufficient stock"
    )

    @ExceptionHandler(InvalidQuantityException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidQuantity(ex: InvalidQuantityException) = ErrorResponse(
        error = "QUANTITY_INVALID",
        message = ex.message ?: "Invalid quantity"
    )

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgument(ex: IllegalArgumentException) = ErrorResponse(
        error = "INVALID_INPUT",
        message = ex.message ?: "Invalid request"
    )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ErrorResponse {
        val errors = ex.bindingResult.fieldErrors
            .map { "${it.field}: ${it.defaultMessage}" }
            .joinToString("; ")
        return ErrorResponse(
            error = "VALIDATION_ERROR",
            message = errors.ifEmpty { "Validation failed" }
        )
    }

    // ===========================================
    // 409 Conflict - State conflicts
    // ===========================================

    @ExceptionHandler(IllegalStateException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleIllegalState(ex: IllegalStateException) = ErrorResponse(
        error = "INVALID_STATE_TRANSITION",
        message = ex.message ?: "Invalid state transition"
    )

    // ===========================================
    // 500 Internal Server Error - Unexpected errors
    // ===========================================

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    error = "INTERNAL_ERROR",
                    message = "An unexpected error occurred"
                )
            )
    }
}
