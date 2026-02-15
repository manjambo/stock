package com.gaywood.stock.domain.staff.model

import java.util.UUID

@JvmInline
value class StaffId(val value: String) {
    init {
        require(value.isNotBlank()) { "Staff ID cannot be blank" }
    }

    companion object {
        fun generate(): StaffId = StaffId(UUID.randomUUID().toString())

        private val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

        fun isValidUuid(value: String): Boolean = UUID_REGEX.matches(value)
    }

    override fun toString(): String = value
}
