package com.gaywood.stock.domain.staff.model

data class StaffName(val firstName: String, val lastName: String) {

    init {
        require(firstName.isNotBlank()) { "First name cannot be blank" }
        require(lastName.isNotBlank()) { "Last name cannot be blank" }
    }

    val fullName: String
        get() = "$firstName $lastName"

    override fun toString(): String = fullName
}
