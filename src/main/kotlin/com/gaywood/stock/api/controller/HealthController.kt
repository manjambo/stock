package com.gaywood.stock.api.controller

import com.gaywood.stock.infrastructure.config.Features
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.togglz.core.manager.FeatureManager
import javax.sql.DataSource

@RestController
@RequestMapping("/health")
class HealthController(
    private val dataSource: DataSource,
    private val featureManager: FeatureManager
) {

    @GetMapping
    fun health(): ResponseEntity<HealthResponse> = readiness()

    @GetMapping("/ready")
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
    fun liveness(): ResponseEntity<LivenessResponse> {
        return ResponseEntity.ok(LivenessResponse(status = "UP"))
    }

    @GetMapping("/features")
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

data class HealthResponse(
    val status: String,
    val components: Map<String, ComponentHealth>
)

data class LivenessResponse(
    val status: String
)

data class ComponentHealth(
    val status: String,
    val error: String? = null
)

data class FeatureFlagsResponse(
    val features: Map<String, Boolean>
)
