package com.gaywood.stock.domain.staff.repository

import com.gaywood.stock.domain.staff.model.Staff
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.domain.staff.model.StaffRole

interface StaffRepository {
    fun save(staff: Staff): Staff
    fun findById(id: StaffId): Staff?
    fun findByRole(roleType: Class<out StaffRole>): List<Staff>
    fun findAll(): List<Staff>
    fun delete(id: StaffId)
}
