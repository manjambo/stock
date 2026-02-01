package com.gaywood.stock.infrastructure.persistence.jpa.converters

import com.gaywood.stock.domain.staff.model.StaffRole
import com.gaywood.stock.domain.stock.model.StockLocation
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StaffRoleConverter : AttributeConverter<StaffRole, String> {

    override fun convertToDatabaseColumn(attribute: StaffRole?): String? {
        return attribute?.let { encodeRole(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): StaffRole? {
        return dbData?.let { decodeRole(it) }
    }

    companion object {
        fun encodeRole(role: StaffRole): String {
            return when (role) {
                is StaffRole.Worker -> "WORKER:${role.location.name}"
                is StaffRole.Manager -> "MANAGER:${role.locations.joinToString(",") { it.name }}"
            }
        }

        fun decodeRole(value: String): StaffRole {
            val parts = value.split(":", limit = 2)
            val roleType = parts[0]
            val locationsStr = parts.getOrElse(1) { "" }

            return when (roleType) {
                "WORKER" -> {
                    val location = StockLocation.valueOf(locationsStr)
                    StaffRole.Worker(location)
                }
                "MANAGER" -> {
                    val locations = locationsStr.ifBlank { null }
                        ?.split(",")
                        ?.map { StockLocation.valueOf(it) }
                        ?.toSet()
                        ?: StockLocation.entries.toSet()
                    StaffRole.Manager(locations)
                }
                else -> throw IllegalArgumentException("Unknown role type: $roleType")
            }
        }
    }
}
