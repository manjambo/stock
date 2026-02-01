package com.gaywood.stock.infrastructure.persistence.jpa.entity

import com.gaywood.stock.domain.menu.model.Menu
import com.gaywood.stock.domain.menu.model.MenuId
import com.gaywood.stock.domain.menu.model.MenuType
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "menus")
class MenuEntity(
    @Id
    @Column(name = "id", length = 36)
    var id: String = "",

    @Column(name = "name", length = 255, nullable = false)
    var name: String = "",

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_type", length = 20, nullable = false)
    var menuType: MenuType = MenuType.FOOD,

    @Column(name = "active", nullable = false)
    var active: Boolean = true,

    @OneToMany(mappedBy = "menu", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var items: MutableList<MenuItemEntity> = mutableListOf(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    fun toDomain(): Menu {
        return Menu.create(
            id = MenuId(id),
            name = name,
            description = description,
            type = menuType,
            items = items.map { it.toDomain() },
            active = active
        )
    }

    fun updateFrom(menu: Menu) {
        name = menu.name
        description = menu.description
        active = menu.active
        replaceItemsWith(menu.items)
    }

    private fun replaceItemsWith(newItems: List<com.gaywood.stock.domain.menu.model.MenuItem>) {
        items.clear()
        newItems.forEach { item ->
            items.add(MenuItemEntity.from(item, this))
        }
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }

    companion object {
        fun from(menu: Menu): MenuEntity {
            val entity = MenuEntity(
                id = menu.id.value,
                name = menu.name,
                description = menu.description,
                menuType = menu.type,
                active = menu.active,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            entity.items = menu.items.map { item ->
                MenuItemEntity.from(item, entity)
            }.toMutableList()
            return entity
        }
    }
}
