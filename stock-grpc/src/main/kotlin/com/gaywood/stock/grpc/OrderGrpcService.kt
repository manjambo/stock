package com.gaywood.stock.grpc

import com.gaywood.stock.application.OrderService
import com.gaywood.stock.domain.shared.OrderNotFoundException
import com.gaywood.stock.grpc.v1.*
import net.devh.boot.grpc.server.service.GrpcService

/**
 * gRPC service implementation for Order operations.
 * Provides full API parity with the REST OrderController.
 */
@GrpcService
class OrderGrpcService(
    private val orderService: OrderService
) : OrderServiceGrpcKt.OrderServiceCoroutineImplBase() {

    override suspend fun createOrder(request: CreateOrderRequest): OrderResponse {
        val order = orderService.placeOrder(
            staffId = request.staffId,
            tableNumber = if (request.hasTableNumber()) request.tableNumber else null,
            items = request.itemsList.map { item ->
                OrderService.OrderItemInput(
                    menuItemId = item.menuItemId,
                    quantity = item.quantity,
                    notes = item.notes
                )
            }
        )
        return orderResponse {
            this.order = order.toProto()
        }
    }

    override suspend fun getOrder(request: GetOrderRequest): OrderResponse {
        val order = orderService.getOrder(request.orderId)
            ?: throw OrderNotFoundException(request.orderId)
        return orderResponse {
            this.order = order.toProto()
        }
    }

    override suspend fun listActiveOrders(request: ListActiveOrdersRequest): ListOrdersResponse {
        val pageable = request.pagination.toSpringPageable()
        val page = orderService.getActiveOrders(pageable)
        return listOrdersResponse {
            orders.addAll(page.content.map { it.toProto() })
            pageInfo = page.toPageInfo()
        }
    }

    override suspend fun getBill(request: GetBillRequest): BillResponse {
        val bill = orderService.getBill(request.orderId)
        return billResponse {
            this.bill = bill.toProto()
        }
    }

    override suspend fun updateOrderStatus(request: UpdateOrderStatusRequest): OrderResponse {
        val domainStatus = request.status.toDomain()
        val order = orderService.updateOrderStatus(request.orderId, domainStatus)
        return orderResponse {
            this.order = order.toProto()
        }
    }

    override suspend fun cancelOrder(request: CancelOrderRequest): OrderResponse {
        val order = orderService.cancelOrder(request.orderId)
        return orderResponse {
            this.order = order.toProto()
        }
    }

    override suspend fun listOrdersByTable(request: ListOrdersByTableRequest): ListOrdersResponse {
        val pageable = request.pagination.toSpringPageable()
        val page = orderService.getOrdersByTable(request.tableNumber, pageable)
        return listOrdersResponse {
            orders.addAll(page.content.map { it.toProto() })
            pageInfo = page.toPageInfo()
        }
    }
}
