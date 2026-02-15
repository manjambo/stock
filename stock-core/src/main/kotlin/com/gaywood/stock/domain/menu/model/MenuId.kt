package com.gaywood.stock.domain.menu.model

import java.util.UUID

@JvmInline
value class MenuId(val value: String) {
    init {
        require(value.isNotBlank()) { "Menu ID cannot be blank" }
    }

    companion object {
        fun generate(): MenuId = MenuId(UUID.randomUUID().toString())

        private val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

        fun isValidUuid(value: String): Boolean = UUID_REGEX.matches(value)
    }

    override fun toString(): String = value
}
