package com.gaywood.stock.api.controller

import com.gaywood.stock.infrastructure.config.Features
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.extensions.Extension
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.togglz.core.manager.FeatureManager
import javax.sql.DataSource

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Health check endpoints for monitoring and Kubernetes probes")
class HealthController(
    private val dataSource: DataSource,
    private val featureManager: FeatureManager
) {

    @GetMapping
    @Operation(
        summary = "Health check",
        description = "Returns the overall health status of the application (alias for /ready)",
        extensions = [Extension(
            name = "x-42c-no-authentication",
            properties = [ExtensionProperty(name = "x-42c-no-authentication", value = "true", parseValue = true)]
        )]
    )
    @SecurityRequirements
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "All components are healthy",
            content = [Content(schema = Schema(implementation = HealthResponse::class))]),
        ApiResponse(responseCode = "503", description = "One or more components are unhealthy",
            content = [Content(schema = Schema(implementation = HealthResponse::class))])
    ])
    fun health(): ResponseEntity<HealthResponse> = readiness()

    @GetMapping("/ready")
    @Operation(
        summary = "Readiness probe",
        description = "Kubernetes readiness probe - checks if the application is ready to receive traffic",
        extensions = [Extension(
            name = "x-42c-no-authentication",
            properties = [ExtensionProperty(name = "x-42c-no-authentication", value = "true", parseValue = true)]
        )]
    )
    @SecurityRequirements
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Application is ready",
            content = [Content(schema = Schema(implementation = HealthResponse::class))]),
        ApiResponse(responseCode = "503", description = "Application is not ready",
            content = [Content(schema = Schema(implementation = HealthResponse::class))])
    ])
    fun readiness(): ResponseEntity<HealthResponse> {
        val databaseHealth = checkDatabaseHealth()
        val togglzHealth = checkTogglzHealth()
        val overallStatus = determineOverallStatus(databaseHealth, togglzHealth)

        val response = HealthResponse(
            status = overallStatus,
            components = mapOf(
                "database" to databaseHealth,
                "featureFlags" to togglzHealth
            )
        )

        return when (overallStatus) {
            "UP" -> ResponseEntity.ok(response)
            else -> ResponseEntity.status(503).body(response)
        }
    }

    @GetMapping("/live")
    @Operation(
        summary = "Liveness probe",
        description = "Kubernetes liveness probe - checks if the application is running",
        extensions = [Extension(
            name = "x-42c-no-authentication",
            properties = [ExtensionProperty(name = "x-42c-no-authentication", value = "true", parseValue = true)]
        )]
    )
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Application is alive",
        content = [Content(schema = Schema(implementation = LivenessResponse::class))])
    fun liveness(): ResponseEntity<LivenessResponse> {
        return ResponseEntity.ok(LivenessResponse(status = "UP"))
    }

    @GetMapping("/features")
    @Operation(
        summary = "Feature flags status",
        description = "Returns the current state of all feature flags",
        extensions = [Extension(
            name = "x-42c-no-authentication",
            properties = [ExtensionProperty(name = "x-42c-no-authentication", value = "true", parseValue = true)]
        )]
    )
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Feature flags retrieved",
        content = [Content(schema = Schema(implementation = FeatureFlagsResponse::class))])
    fun features(): ResponseEntity<FeatureFlagsResponse> {
        val flags = Features.entries.associate { feature ->
            feature.name to featureManager.isActive(feature)
        }
        return ResponseEntity.ok(FeatureFlagsResponse(flags))
    }

    private fun checkDatabaseHealth(): ComponentHealth {
        return try {
            dataSource.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT 1").use { resultSet ->
                        resultSet.next()
                    }
                }
            }
            ComponentHealth(status = "UP")
        } catch (e: Exception) {
            ComponentHealth(status = "DOWN", error = e.message)
        }
    }

    private fun checkTogglzHealth(): ComponentHealth {
        return try {
            featureManager.features
            ComponentHealth(status = "UP")
        } catch (e: Exception) {
            ComponentHealth(status = "DOWN", error = e.message)
        }
    }

    private fun determineOverallStatus(vararg components: ComponentHealth): String =
        components.all { it.status == "UP" }
            .let { allUp -> if (allUp) "UP" else "DOWN" }
}

@Schema(description = "Health check response with component statuses")
data class HealthResponse(
    @Schema(
        description = "Overall health status",
        example = "UP",
        allowableValues = ["UP", "DOWN"],
        maxLength = 10
    )
    val status: String,
    @Schema(
        description = "Health status of individual components",
        example = """{"database": {"status": "UP"}, "featureFlags": {"status": "UP"}}"""
    )
    val components: Map<String, ComponentHealth>
)

@Schema(description = "Liveness probe response")
data class LivenessResponse(
    @Schema(
        description = "Liveness status",
        example = "UP",
        allowableValues = ["UP"],
        maxLength = 10
    )
    val status: String
)

@Schema(description = "Individual component health status")
data class ComponentHealth(
    @Schema(
        description = "Component health status",
        example = "UP",
        allowableValues = ["UP", "DOWN"],
        maxLength = 10
    )
    val status: String,
    @Schema(
        description = "Error message if component is unhealthy",
        example = "Connection refused",
        nullable = true,
        maxLength = 500
    )
    val error: String? = null
)

@Schema(description = "Feature flags status response")
data class FeatureFlagsResponse(
    @Schema(
        description = "Map of feature flag names to their enabled status",
        example = """{"ASYNC_ORDER_PROCESSING": true, "REAL_TIME_STOCK_UPDATES": false}"""
    )
    val features: Map<String, Boolean>
)
