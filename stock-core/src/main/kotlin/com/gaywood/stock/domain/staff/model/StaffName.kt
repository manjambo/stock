package com.gaywood.stock.domain.staff.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class StaffName(
    @field:NotBlank(message = "First name cannot be blank")
    @field:Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name cannot be blank")
    @field:Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    val lastName: String
) {

    init {
        require(firstName.isNotBlank()) { "First name cannot be blank" }
        require(lastName.isNotBlank()) { "Last name cannot be blank" }
    }

    val fullName: String
        get() = "$firstName $lastName"

    override fun toString(): String = fullName
}
