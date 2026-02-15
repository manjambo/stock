package com.gaywood.stock.infrastructure.persistence.jpa.entity

import com.gaywood.stock.domain.staff.model.Staff
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.domain.staff.model.StaffName
import com.gaywood.stock.domain.staff.model.StaffRole
import com.gaywood.stock.infrastructure.persistence.jpa.converters.StaffRoleConverter
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "staff")
class StaffEntity(
    @Id
    @Column(name = "id", length = 36)
    var id: String = "",

    @Column(name = "first_name", length = 100, nullable = false)
    var firstName: String = "",

    @Column(name = "last_name", length = 100, nullable = false)
    var lastName: String = "",

    @Column(name = "role", length = 255, nullable = false)
    @Convert(converter = StaffRoleConverter::class)
    var role: StaffRole = StaffRole.Worker(com.gaywood.stock.domain.stock.model.StockLocation.BAR),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    fun toDomain(): Staff {
        return Staff.create(
            id = StaffId(id),
            name = StaffName(firstName, lastName),
            role = role
        )
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }

    companion object {
        fun from(staff: Staff): StaffEntity {
            return StaffEntity(
                id = staff.id.value,
                firstName = staff.name.firstName,
                lastName = staff.name.lastName,
                role = staff.role,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        }
    }
}
