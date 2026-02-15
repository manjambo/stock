package com.gaywood.stock.grpc

import com.gaywood.stock.domain.menu.model.Price
import com.gaywood.stock.domain.order.model.Bill
import com.gaywood.stock.domain.order.model.BillLineItem
import com.gaywood.stock.domain.order.model.Order
import com.gaywood.stock.domain.order.model.OrderItem
import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.grpc.v1.*
import com.google.protobuf.Timestamp
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.Instant
import com.gaywood.stock.grpc.v1.Order as ProtoOrder
import com.gaywood.stock.grpc.v1.OrderItem as ProtoOrderItem
import com.gaywood.stock.grpc.v1.OrderStatus as ProtoOrderStatus
import com.gaywood.stock.grpc.v1.Bill as ProtoBill
import com.gaywood.stock.grpc.v1.BillLineItem as ProtoBillLineItem
import com.gaywood.stock.grpc.v1.Price as ProtoPrice

/**
 * Maps domain Order to proto Order
 */
fun Order.toProto(): ProtoOrder = order {
    id = this@toProto.id.value
    status = this@toProto.status.toProto()
    if (this@toProto.tableNumber != null) {
        tableNumber = this@toProto.tableNumber!!
    }
    staffId = this@toProto.staffId.value
    items.addAll(this@toProto.items.map { it.toProto() })
    totalAmount = this@toProto.totalAmount.toProto()
    createdAt = this@toProto.createdAt.toProto()
}

/**
 * Maps domain OrderItem to proto OrderItem
 */
fun OrderItem.toProto(): ProtoOrderItem = orderItem {
    id = this@toProto.id.value
    menuItemId = this@toProto.menuItemId.value
    menuItemName = this@toProto.menuItemName
    quantity = this@toProto.quantity
    unitPrice = this@toProto.unitPrice.toProto()
    totalPrice = this@toProto.totalPrice.toProto()
    notes = this@toProto.notes
}

/**
 * Maps domain OrderStatus to proto OrderStatus
 */
fun OrderStatus.toProto(): ProtoOrderStatus = when (this) {
    OrderStatus.PENDING -> ProtoOrderStatus.ORDER_STATUS_PENDING
    OrderStatus.IN_PROGRESS -> ProtoOrderStatus.ORDER_STATUS_IN_PROGRESS
    OrderStatus.READY -> ProtoOrderStatus.ORDER_STATUS_READY
    OrderStatus.SERVED -> ProtoOrderStatus.ORDER_STATUS_SERVED
    OrderStatus.PAID -> ProtoOrderStatus.ORDER_STATUS_PAID
    OrderStatus.CANCELLED -> ProtoOrderStatus.ORDER_STATUS_CANCELLED
}

/**
 * Maps proto OrderStatus to domain OrderStatus
 */
fun ProtoOrderStatus.toDomain(): OrderStatus = when (this) {
    ProtoOrderStatus.ORDER_STATUS_PENDING -> OrderStatus.PENDING
    ProtoOrderStatus.ORDER_STATUS_IN_PROGRESS -> OrderStatus.IN_PROGRESS
    ProtoOrderStatus.ORDER_STATUS_READY -> OrderStatus.READY
    ProtoOrderStatus.ORDER_STATUS_SERVED -> OrderStatus.SERVED
    ProtoOrderStatus.ORDER_STATUS_PAID -> OrderStatus.PAID
    ProtoOrderStatus.ORDER_STATUS_CANCELLED -> OrderStatus.CANCELLED
    ProtoOrderStatus.ORDER_STATUS_UNSPECIFIED, ProtoOrderStatus.UNRECOGNIZED ->
        throw IllegalArgumentException("Invalid order status")
}

/**
 * Maps domain Bill to proto Bill
 */
fun Bill.toProto(): ProtoBill = bill {
    orderId = this@toProto.orderId.value
    items.addAll(this@toProto.items.map { it.toProto() })
    totalAmount = this@toProto.totalAmount.toProto()
    if (this@toProto.tableNumber != null) {
        tableNumber = this@toProto.tableNumber!!
    }
    generatedAt = this@toProto.generatedAt.toProto()
    formattedBill = this@toProto.formatAsText()
}

/**
 * Maps domain BillLineItem to proto BillLineItem
 */
fun BillLineItem.toProto(): ProtoBillLineItem = billLineItem {
    description = this@toProto.description
    quantity = this@toProto.quantity
    unitPrice = this@toProto.unitPrice.toProto()
    totalPrice = this@toProto.totalPrice.toProto()
}

/**
 * Maps domain Price to proto Price.
 * Converts BigDecimal amount to minor units (pence) for the proto message.
 */
fun Price.toProto(): ProtoPrice = price {
    // Convert BigDecimal to pence (multiply by 100)
    amountMinor = this@toProto.amount.multiply(BigDecimal(100)).toLong()
    currency = this@toProto.currency.currencyCode
    formatted = this@toProto.toString()
}

/**
 * Maps Instant to proto Timestamp
 */
fun Instant.toProto(): Timestamp = Timestamp.newBuilder()
    .setSeconds(epochSecond)
    .setNanos(nano)
    .build()

/**
 * Maps PageRequest proto to Spring PageRequest
 */
fun com.gaywood.stock.grpc.v1.PageRequest?.toSpringPageable(
    defaultSize: Int = 20,
    maxSize: Int = 100
): org.springframework.data.domain.Pageable {
    val page = this?.page?.coerceAtLeast(0) ?: 0
    val size = (this?.size?.coerceIn(1, maxSize) ?: defaultSize)
    return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
}

/**
 * Maps Spring Page to proto PageInfo
 */
fun <T> Page<T>.toPageInfo(): PageInfo = pageInfo {
    page = this@toPageInfo.number
    size = this@toPageInfo.size
    totalElements = this@toPageInfo.totalElements
    totalPages = this@toPageInfo.totalPages
    hasNext = this@toPageInfo.hasNext()
    hasPrevious = this@toPageInfo.hasPrevious()
}
