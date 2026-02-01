package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.infrastructure.persistence.jpa.entity.StaffEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StaffJpaRepository : JpaRepository<StaffEntity, String>
