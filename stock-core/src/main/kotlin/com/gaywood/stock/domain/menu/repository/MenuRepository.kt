package com.gaywood.stock.domain.menu.repository

import com.gaywood.stock.domain.menu.model.Menu
import com.gaywood.stock.domain.menu.model.MenuId
import com.gaywood.stock.domain.menu.model.MenuType

interface MenuRepository {
    fun save(menu: Menu): Menu
    fun findById(id: MenuId): Menu?
    fun findByType(type: MenuType): List<Menu>
    fun findByName(name: String): Menu?
    fun findActive(): List<Menu>
    fun findAll(): List<Menu>
    fun delete(id: MenuId)
}
