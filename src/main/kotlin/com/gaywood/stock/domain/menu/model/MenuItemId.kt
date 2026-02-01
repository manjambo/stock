package com.gaywood.stock.domain.menu.model

import java.util.UUID

@JvmInline
value class MenuItemId(val value: String) {
    companion object {
        fun generate(): MenuItemId = MenuItemId(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}
