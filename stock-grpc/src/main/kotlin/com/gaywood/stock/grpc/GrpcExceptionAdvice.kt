package com.gaywood.stock.grpc

import com.gaywood.stock.domain.shared.*
import io.grpc.Status
import io.grpc.StatusException
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler

/**
 * Global exception handler for gRPC services.
 * Maps domain exceptions to appropriate gRPC status codes.
 */
@GrpcAdvice
class GrpcExceptionAdvice {

    @GrpcExceptionHandler(OrderNotFoundException::class)
    fun handleOrderNotFound(e: OrderNotFoundException): StatusException {
        return Status.NOT_FOUND
            .withDescription(e.message)
            .asException()
    }

    @GrpcExceptionHandler(StaffNotFoundException::class)
    fun handleStaffNotFound(e: StaffNotFoundException): StatusException {
        return Status.NOT_FOUND
            .withDescription(e.message)
            .asException()
    }

    @GrpcExceptionHandler(MenuItemNotFoundException::class)
    fun handleMenuItemNotFound(e: MenuItemNotFoundException): StatusException {
        return Status.NOT_FOUND
            .withDescription(e.message)
            .asException()
    }

    @GrpcExceptionHandler(StockItemNotFoundException::class)
    fun handleStockItemNotFound(e: StockItemNotFoundException): StatusException {
        return Status.NOT_FOUND
            .withDescription(e.message)
            .asException()
    }

    @GrpcExceptionHandler(PermissionDeniedException::class)
    fun handlePermissionDenied(e: PermissionDeniedException): StatusException {
        return Status.PERMISSION_DENIED
            .withDescription(e.message)
            .asException()
    }

    @GrpcExceptionHandler(LocationAccessDeniedException::class)
    fun handleLocationAccessDenied(e: LocationAccessDeniedException): StatusException {
        return Status.PERMISSION_DENIED
            .withDescription(e.message)
            .asException()
    }

    @GrpcExceptionHandler(InsufficientStockException::class)
    fun handleInsufficientStock(e: InsufficientStockException): StatusException {
        return Status.FAILED_PRECONDITION
            .withDescription(e.message)
            .asException()
    }

    @GrpcExceptionHandler(InvalidQuantityException::class)
    fun handleInvalidQuantity(e: InvalidQuantityException): StatusException {
        return Status.INVALID_ARGUMENT
            .withDescription(e.message)
            .asException()
    }

    @GrpcExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): StatusException {
        return Status.INVALID_ARGUMENT
            .withDescription(e.message)
            .asException()
    }

    @GrpcExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(e: IllegalStateException): StatusException {
        return Status.FAILED_PRECONDITION
            .withDescription(e.message)
            .asException()
    }

    @GrpcExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): StatusException {
        return Status.INTERNAL
            .withDescription("Internal server error")
            .withCause(e)
            .asException()
    }
}
