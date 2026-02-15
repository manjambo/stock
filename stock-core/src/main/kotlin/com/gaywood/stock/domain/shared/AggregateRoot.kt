package com.gaywood.stock.domain.shared

abstract class AggregateRoot<ID> : Entity<ID> {
    private val _domainEvents: MutableList<DomainEvent> = mutableListOf()

    val domainEvents: List<DomainEvent>
        get() = _domainEvents.toList()

    protected fun registerEvent(event: DomainEvent) {
        _domainEvents.add(event)
    }

    fun clearEvents() {
        _domainEvents.clear()
    }
}
