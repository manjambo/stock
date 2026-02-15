# Stock Management System Overview

This document provides visual diagrams explaining the architecture and flow of the stock management system.

## Multi-Module Structure

```mermaid
flowchart TB
    subgraph STOCK["stock (Root Project)"]
        subgraph CORE["stock-core"]
            Domain[Domain Layer]
            App[Application Layer]
            API[REST API Layer]
            Infra[Infrastructure Layer]
        end

        subgraph GRPC["stock-grpc"]
            Proto[Proto Definitions]
            GrpcService[gRPC Services]
            Mappers[Proto Mappers]
        end

        subgraph BOOT["stock-boot"]
            Application[Application.kt]
            Config[Configuration]
            Migrations[Flyway Migrations]
            Tests[Integration Tests]
        end
    end

    GRPC -->|depends on| CORE
    BOOT -->|depends on| CORE
    BOOT -->|depends on| GRPC

    Client1((REST Client)) -->|:8080| API
    Client2((gRPC Client)) -->|:9090| GrpcService
```

## Architecture Overview

```mermaid
flowchart TB
    subgraph GATEWAY["API Gateway Layer"]
        subgraph REST["REST (stock-core)"]
            Controller[OrderController]
            DTO[DTOs: Request/Response]
        end
        subgraph GRPC["gRPC (stock-grpc)"]
            GrpcSvc[OrderGrpcService]
            ProtoMsg[Proto Messages]
        end
    end

    subgraph APP["Application Layer (stock-core)"]
        Service[OrderService]
    end

    subgraph DOMAIN["Domain Layer (Pure Kotlin - No Frameworks!)"]
        Order[Order Aggregate]
        Stock[StockItem Aggregate]
        Menu[Menu Aggregate]
        Staff[Staff Aggregate]
        VO[Value Objects: Price, Quantity, IDs]
        Events[Domain Events]
        RepoInterface[Repository Interfaces]
    end

    subgraph INFRA["Infrastructure Layer (stock-core)"]
        Adapter[JpaOrderRepositoryAdapter]
        Entity[JPA Entities]
        JpaRepo[Spring Data Repository]
    end

    DB[(PostgreSQL)]

    HTTP((HTTP :8080)) --> Controller
    GRPC_REQ((gRPC :9090)) --> GrpcSvc
    Controller --> Service
    GrpcSvc --> Service
    Service --> Order
    Service --> RepoInterface
    RepoInterface -.->|implements| Adapter
    Adapter --> Entity
    Entity --> JpaRepo
    JpaRepo --> DB
```

## gRPC Service Mapping

```mermaid
flowchart LR
    subgraph REST["REST Endpoints"]
        R1["POST /orders"]
        R2["GET /orders"]
        R3["GET /orders/{id}"]
        R4["GET /orders/{id}/bill"]
        R5["POST /orders/{id}/status"]
        R6["POST /orders/{id}/cancel"]
        R7["GET /health/*"]
    end

    subgraph GRPC["gRPC Methods"]
        G1["OrderService.CreateOrder"]
        G2["OrderService.ListActiveOrders"]
        G3["OrderService.GetOrder"]
        G4["OrderService.GetBill"]
        G5["OrderService.UpdateOrderStatus"]
        G6["OrderService.CancelOrder"]
        G7["HealthService.Check/Ready/Live"]
    end

    R1 <-.->|parity| G1
    R2 <-.->|parity| G2
    R3 <-.->|parity| G3
    R4 <-.->|parity| G4
    R5 <-.->|parity| G5
    R6 <-.->|parity| G6
    R7 <-.->|parity| G7
```

## Order Lifecycle

```mermaid
stateDiagram-v2
    [*] --> PENDING: Order created
    PENDING --> IN_PROGRESS: Staff starts preparing
    IN_PROGRESS --> READY: Food/drinks ready
    READY --> SERVED: Delivered to table
    SERVED --> PAID: Customer pays
    PAID --> [*]

    PENDING --> CANCELLED: Order cancelled
    IN_PROGRESS --> CANCELLED: Order cancelled
    READY --> CANCELLED: Order cancelled
    CANCELLED --> [*]
```

## Request Flow: Placing an Order (REST)

```mermaid
sequenceDiagram
    participant Client
    participant Controller as OrderController
    participant Service as OrderService
    participant Domain as Order.create()
    participant Adapter as JpaOrderRepositoryAdapter
    participant DB as PostgreSQL

    Client->>Controller: POST /orders {staffId, items}
    Controller->>Controller: Validate @Valid
    Controller->>Service: placeOrder()
    Service->>Service: Verify staff exists
    Service->>Service: Fetch menu items
    Service->>Domain: Order.create(items, staff)
    Domain-->>Domain: Register OrderCreated event
    Domain-->>Service: Return Order
    Service->>Adapter: save(order)
    Adapter->>Adapter: Convert Order → OrderEntity
    Adapter->>DB: INSERT orders, order_items
    DB-->>Adapter: Saved
    Adapter->>Adapter: Convert OrderEntity → Order
    Adapter-->>Service: Return Order
    Service-->>Controller: Return Order
    Controller->>Controller: Map to OrderResponse
    Controller-->>Client: 201 Created {order}
```

## Request Flow: Placing an Order (gRPC)

```mermaid
sequenceDiagram
    participant Client as gRPC Client
    participant GrpcSvc as OrderGrpcService
    participant Service as OrderService
    participant Domain as Order.create()
    participant Adapter as JpaOrderRepositoryAdapter
    participant DB as PostgreSQL

    Client->>GrpcSvc: CreateOrder(CreateOrderRequest)
    GrpcSvc->>Service: placeOrder()
    Service->>Service: Verify staff exists
    Service->>Service: Fetch menu items
    Service->>Domain: Order.create(items, staff)
    Domain-->>Domain: Register OrderCreated event
    Domain-->>Service: Return Order
    Service->>Adapter: save(order)
    Adapter->>DB: INSERT orders, order_items
    DB-->>Adapter: Saved
    Adapter-->>Service: Return Order
    Service-->>GrpcSvc: Return Order
    GrpcSvc->>GrpcSvc: Map to OrderResponse proto
    GrpcSvc-->>Client: OrderResponse
```

## Domain Layer Structure

```mermaid
classDiagram
    class AggregateRoot~ID~ {
        +domainEvents: List~DomainEvent~
        #registerEvent(event)
        +clearDomainEvents()
    }

    class Order {
        -id: OrderId
        -status: OrderStatus
        -items: List~OrderItem~
        -tableNumber: Int
        -staffId: StaffId
        +create() Order
        +addItem(item)
        +updateStatus(status)
        +generateBill() Bill
    }

    class OrderItem {
        -menuItemId: MenuItemId
        -menuItemName: String
        -quantity: Int
        -unitPrice: Price
        -notes: String
    }

    class Price {
        -amount: BigDecimal
        -currency: Currency
        +plus(other) Price
        +times(quantity) Price
    }
    note for Price "Value Object"

    class OrderId {
        -value: String
        +generate() OrderId
    }
    note for OrderId "Value Object"

    AggregateRoot <|-- Order
    Order *-- OrderItem
    OrderItem --> Price
    Order --> OrderId
```

## Staff Permissions

```mermaid
flowchart LR
    subgraph Worker["Worker (Location-Specific)"]
        W_VIEW[VIEW_STOCK]
        W_ADD[ADD_STOCK]
        W_REMOVE[REMOVE_STOCK]
        W_ADJUST[ADJUST_STOCK]
        W_MANAGE[MANAGE_STAFF]
    end

    subgraph Manager["Manager (All Access)"]
        M_VIEW[VIEW_STOCK]
        M_ADD[ADD_STOCK]
        M_REMOVE[REMOVE_STOCK]
        M_ADJUST[ADJUST_STOCK]
        M_MANAGE[MANAGE_STAFF]
    end

    BAR[Bar Location] --> Worker
    KITCHEN[Kitchen Location] --> Worker
    ALL[All Locations] --> Manager

    style W_ADJUST fill:#f66
    style W_MANAGE fill:#f66
```

## Proto Message Structure

```mermaid
classDiagram
    class CreateOrderRequest {
        +staff_id: string
        +table_number: int32 [optional]
        +items: OrderItemInput[]
    }

    class OrderItemInput {
        +menu_item_id: string
        +quantity: int32
        +notes: string
    }

    class Order {
        +id: string
        +status: OrderStatus
        +table_number: int32 [optional]
        +staff_id: string
        +items: OrderItem[]
        +total_amount: Price
        +created_at: Timestamp
    }

    class Price {
        +amount_minor: int64
        +currency: string
        +formatted: string
    }

    CreateOrderRequest *-- OrderItemInput
    Order *-- Price
```

## Snapshot Pattern (OrderItem)

```mermaid
flowchart LR
    subgraph WRONG["Wrong: Reference Only"]
        O1[OrderItem] -->|menuItemId| M1[MenuItem]
        M1 -->|price changes!| P1["15 (was 12)"]
        O1 -.->|shows wrong price| P1
    end

    subgraph RIGHT["Right: Snapshot Values"]
        O2[OrderItem]
        O2 --> N2["menuItemName: 'Fish & Chips'"]
        O2 --> P2["unitPrice: 12.50"]
    end

    style WRONG fill:#fee
    style RIGHT fill:#efe
```

The `OrderItem` stores `menuItemName` and `unitPrice` directly - these are snapshots of what the customer actually ordered. If the menu price changes later, historical orders remain accurate.

## Exception Handling

```mermaid
flowchart TB
    subgraph Domain["Domain Exceptions"]
        OrderNotFound[OrderNotFoundException]
        StaffNotFound[StaffNotFoundException]
        MenuItemNotFound[MenuItemNotFoundException]
        PermissionDenied[PermissionDeniedException]
        InsufficientStock[InsufficientStockException]
    end

    subgraph REST["REST Handler"]
        R404[404 Not Found]
        R403[403 Forbidden]
        R400[400 Bad Request]
    end

    subgraph GRPC["gRPC Handler"]
        G_NOT_FOUND[NOT_FOUND]
        G_PERM[PERMISSION_DENIED]
        G_INVALID[INVALID_ARGUMENT]
        G_PRECOND[FAILED_PRECONDITION]
    end

    OrderNotFound --> R404
    OrderNotFound --> G_NOT_FOUND
    StaffNotFound --> R404
    StaffNotFound --> G_NOT_FOUND
    MenuItemNotFound --> R404
    MenuItemNotFound --> G_NOT_FOUND
    PermissionDenied --> R403
    PermissionDenied --> G_PERM
    InsufficientStock --> R400
    InsufficientStock --> G_PRECOND
```
