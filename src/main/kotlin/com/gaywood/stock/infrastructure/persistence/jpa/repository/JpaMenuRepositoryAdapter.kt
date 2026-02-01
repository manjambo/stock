package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.domain.menu.model.Menu
import com.gaywood.stock.domain.menu.model.MenuId
import com.gaywood.stock.domain.menu.model.MenuType
import com.gaywood.stock.domain.menu.repository.MenuRepository
import com.gaywood.stock.infrastructure.persistence.jpa.entity.MenuEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class JpaMenuRepositoryAdapter(
    private val jpaRepository: MenuJpaRepository
) : MenuRepository {

    override fun save(menu: Menu): Menu {
        val entity = jpaRepository.findById(menu.id.value).orElse(null)
            ?.let { updateEntity(it, menu) }
            ?: MenuEntity.from(menu)
        return jpaRepository.save(entity).toDomain()
    }

    override fun findById(id: MenuId): Menu? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findByType(type: MenuType): List<Menu> {
        return jpaRepository.findByMenuType(type).map { it.toDomain() }
    }

    override fun findByName(name: String): Menu? {
        return jpaRepository.findByName(name)?.toDomain()
    }

    override fun findActive(): List<Menu> {
        return jpaRepository.findByActiveTrue().map { it.toDomain() }
    }

    override fun findAll(): List<Menu> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun delete(id: MenuId) {
        jpaRepository.deleteById(id.value)
    }

    private fun updateEntity(existing: MenuEntity, menu: Menu): MenuEntity {
        existing.updateFrom(menu)
        return existing
    }
}
