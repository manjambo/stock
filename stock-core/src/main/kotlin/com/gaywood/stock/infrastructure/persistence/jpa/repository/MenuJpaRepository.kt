package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.domain.menu.model.MenuType
import com.gaywood.stock.infrastructure.persistence.jpa.entity.MenuEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MenuJpaRepository : JpaRepository<MenuEntity, String> {
    fun findByMenuType(menuType: MenuType): List<MenuEntity>
    fun findByName(name: String): MenuEntity?
    fun findByActiveTrue(): List<MenuEntity>
}
