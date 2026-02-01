package com.gaywood.stock.domain.order.model

import com.gaywood.stock.domain.menu.model.Price
import java.time.Instant

data class BillLineItem(
    val description: String,
    val quantity: Int,
    val unitPrice: Price,
    val totalPrice: Price
)

data class Bill(
    val orderId: OrderId,
    val items: List<BillLineItem>,
    val totalAmount: Price,
    val tableNumber: Int?,
    val generatedAt: Instant
) {
    fun formatAsText(): String {
        val sb = StringBuilder()
        appendHeader(sb)
        items.forEach { appendLineItem(sb, it) }
        appendFooter(sb)
        return sb.toString()
    }

    private fun appendHeader(sb: StringBuilder) {
        sb.appendLine("=".repeat(BILL_WIDTH))
        sb.appendLine("              ITEMISED BILL")
        sb.appendLine("=".repeat(BILL_WIDTH))
        tableNumber?.let { sb.appendLine("Table: $it") }
        sb.appendLine("-".repeat(BILL_WIDTH))
    }

    private fun appendLineItem(sb: StringBuilder, item: BillLineItem) {
        val qtyStr = "${item.quantity}x"
        val priceStr = item.totalPrice.toString()
        val description = formatDescription(item.description, qtyStr, priceStr)
        sb.appendLine("$qtyStr $description $priceStr")
        appendUnitPriceIfMultipleQuantity(sb, item)
    }

    private fun formatDescription(description: String, qtyStr: String, priceStr: String): String {
        val availableWidth = BILL_WIDTH - qtyStr.length - priceStr.length - 4
        return when {
            description.length > availableWidth -> description.take(availableWidth - 3) + "..."
            else -> description.padEnd(availableWidth)
        }
    }

    private fun appendUnitPriceIfMultipleQuantity(sb: StringBuilder, item: BillLineItem) {
        item.takeIf { it.quantity > 1 }?.let {
            sb.appendLine("   @ ${it.unitPrice} each")
        }
    }

    private fun appendFooter(sb: StringBuilder) {
        sb.appendLine("-".repeat(BILL_WIDTH))
        sb.appendLine("TOTAL:".padEnd(TOTAL_LABEL_WIDTH) + totalAmount.toString())
        sb.appendLine("=".repeat(BILL_WIDTH))
        sb.appendLine("Thank you for your visit!")
    }

    companion object {
        private const val BILL_WIDTH = 40
        private const val TOTAL_LABEL_WIDTH = 32
    }
}
