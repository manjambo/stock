package com.gaywood.stock.domain.menu.model

import java.util.UUID

@JvmInline
value class MenuId(val value: String) {
    companion object {
        fun generate(): MenuId = MenuId(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}
