package com.gaywood.stock.domain.staff.model

import java.util.UUID

@JvmInline
value class StaffId(val value: String) {
    companion object {
        fun generate(): StaffId = StaffId(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}
