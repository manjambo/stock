package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.domain.staff.model.Staff
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.domain.staff.model.StaffRole
import com.gaywood.stock.domain.staff.repository.StaffRepository
import com.gaywood.stock.infrastructure.persistence.jpa.entity.StaffEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class JpaStaffRepositoryAdapter(
    private val jpaRepository: StaffJpaRepository
) : StaffRepository {

    override fun save(staff: Staff): Staff {
        val entity = jpaRepository.findById(staff.id.value).orElse(null)
            ?.let { updateEntity(it, staff) }
            ?: StaffEntity.from(staff)
        return jpaRepository.save(entity).toDomain()
    }

    override fun findById(id: StaffId): Staff? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findByRole(roleType: Class<out StaffRole>): List<Staff> {
        return jpaRepository.findAll()
            .filter { roleType.isInstance(it.role) }
            .map { it.toDomain() }
    }

    override fun findAll(): List<Staff> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun delete(id: StaffId) {
        jpaRepository.deleteById(id.value)
    }

    private fun updateEntity(existing: StaffEntity, staff: Staff): StaffEntity {
        existing.firstName = staff.name.firstName
        existing.lastName = staff.name.lastName
        existing.role = staff.role
        return existing
    }
}
