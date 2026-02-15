# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Run all tests
./gradlew.bat test

# Run tests with clean build
./gradlew.bat clean test

# Full build
./gradlew.bat build

# Run a single test class
./gradlew.bat test --tests "com.gaywood.stock.domain.stock.StockItemSpec"

# Run tests matching a pattern
./gradlew.bat test --tests "*OrderSpec"

# Run with test coverage report
./gradlew.bat test jacocoTestReport

# Run specific module
./gradlew.bat :stock-core:build
./gradlew.bat :stock-grpc:build
./gradlew.bat :stock-boot:build

# Start the application
./gradlew.bat :stock-boot:bootRun
```

Coverage reports are generated in `stock-boot/build/reports/jacoco/test/`.

## Project Structure (Multi-Module)

```
stock/
├── stock-core/                    # Core business logic
│   ├── src/main/kotlin/           # Domain, Application, API, Infrastructure
│   └── build.gradle.kts
│
├── stock-grpc/                    # gRPC gateway service
│   ├── src/main/proto/            # Proto definitions
│   ├── src/main/kotlin/           # gRPC service implementations
│   └── build.gradle.kts
│
├── stock-boot/                    # Spring Boot application assembly
│   ├── src/main/kotlin/           # Application.kt entry point
│   ├── src/main/resources/        # application.yml, migrations
│   ├── src/test/kotlin/           # Integration tests
│   └── build.gradle.kts
│
├── settings.gradle.kts            # Module declarations
├── build.gradle.kts               # Root build config
└── gradle/libs.versions.toml      # Version catalog
```

## Architecture

This is a **Domain-Driven Design (DDD)** stock management system for bar and kitchen operations, built with Spring Boot 3.4 and Kotlin.

### Layer Structure (in stock-core)

```
domain/          Pure business logic, no framework dependencies
  ├── shared/    Entity, AggregateRoot, DomainEvent, domain exceptions
  ├── stock/     StockItem aggregate with Quantity, Allergen, StockCategory
  │   └── service/  StockQueryService (business query logic)
  ├── staff/     Staff aggregate with role-based permissions (Worker/Manager)
  │   └── event/    StaffEvent (StaffRoleChanged)
  ├── order/     Order aggregate with Bill generation
  ├── menu/      Menu aggregate with MenuItem (with cached allergens)
  └── operation/ StockOperationService domain service

application/     Orchestration layer (OrderService)

api/             REST controllers, DTOs, exception handlers
  ├── controller/  OrderController, HealthController
  ├── dto/         Request/Response objects
  └── advice/      GlobalExceptionHandler

infrastructure/  Framework integrations
  └── persistence/jpa/
      ├── entity/      JPA entities with toDomain()/from() methods
      ├── repository/  Spring Data JPA repositories + adapter classes
      └── converters/  AttributeConverters for sealed classes and value objects
```

### gRPC Gateway (stock-grpc)

The gRPC module provides full API parity with REST:

| REST Endpoint | gRPC Service | RPC Method |
|---------------|--------------|------------|
| `POST /orders` | OrderService | CreateOrder |
| `GET /orders` | OrderService | ListActiveOrders |
| `GET /orders/{id}` | OrderService | GetOrder |
| `GET /orders/{id}/bill` | OrderService | GetBill |
| `POST /orders/{id}/status` | OrderService | UpdateOrderStatus |
| `POST /orders/{id}/cancel` | OrderService | CancelOrder |
| `GET /orders/table/{n}` | OrderService | ListOrdersByTable |
| `GET /health/*` | HealthService | Check, Ready, Live, Features |

**Ports:**
- REST API: 8080
- gRPC: 9090

### Key Patterns

- **Aggregates**: StockItem, Staff, Order, Menu are aggregate roots with their own identity
- **Value Objects**: Quantity, Price, StockItemId, MenuItemId, etc. are immutable
- **Domain Events**: StockEvent sealed class (StockAdded, StockRemoved, LowStockAlertRaised, etc.), StaffEvent sealed class (StaffRoleChanged)
- **Domain Services**: StockQueryService (allergen and low-stock queries), StockOperationService (staff-authorized operations)
- **Repository Pattern**: Interfaces in domain, JPA implementations in infrastructure via adapter classes

### Type Mapping

Domain sealed classes are mapped to JPA via custom converters:
- `StockCategory` (Bar.Spirits, Kitchen.Proteins, etc.) → String
- `StaffRole` (Worker, Manager) → JSON-like string with location data
- `Quantity` and `Price` → Embeddable classes

### Unit Conflict

The domain has its own `Unit` enum (BOTTLES, LITERS, KILOGRAMS, etc.). When importing both domain models and Kotlin stdlib, use import alias:
```kotlin
import com.gaywood.stock.domain.stock.model.Unit as StockUnit
```

## Testing

**Framework**: Kotest 5.9.1 with Spring extensions

**Test Styles**: FunSpec for Spring integration tests, BehaviorSpec for unit tests

**Test Fixtures**: Located in `stock-boot/src/test/kotlin/com/gaywood/stock/fixtures/`
- `StockFixtures` - Stock item builders
- `StaffFixtures` - Staff member builders (barWorker, kitchenWorker, manager)
- `MenuFixtures` - Menu and MenuItem builders
- `OrderFixtures` - Order builders

**Integration Tests**: Use `@SpringBootTest` with `@ActiveProfiles("test")` and H2 database

## Staff Permissions

Workers have location-specific access (BAR or KITCHEN only). Managers have all permissions across all locations.

| Permission | Worker | Manager |
|------------|:------:|:-------:|
| VIEW_STOCK, ADD_STOCK, REMOVE_STOCK | ✓ | ✓ |
| ADJUST_STOCK, SET_THRESHOLDS, VIEW_AUDIT_LOG, MANAGE_STAFF | ✗ | ✓ |

## Database

- **Production**: PostgreSQL
- **Testing**: H2 in PostgreSQL compatibility mode
- **Migrations**: Flyway (V1-V7 in `stock-boot/src/main/resources/db/migration/`)
