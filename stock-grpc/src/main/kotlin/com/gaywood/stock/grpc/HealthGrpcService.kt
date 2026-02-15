package com.gaywood.stock.grpc

import com.gaywood.stock.grpc.v1.*
import com.gaywood.stock.infrastructure.config.Features
import net.devh.boot.grpc.server.service.GrpcService
import org.togglz.core.manager.FeatureManager
import javax.sql.DataSource

/**
 * gRPC service implementation for Health checks.
 * Provides Kubernetes probes and feature flag status.
 */
@GrpcService
class HealthGrpcService(
    private val dataSource: DataSource,
    private val featureManager: FeatureManager
) : HealthServiceGrpcKt.HealthServiceCoroutineImplBase() {

    override suspend fun check(request: HealthCheckRequest): HealthCheckResponse {
        return ready(request)
    }

    override suspend fun ready(request: HealthCheckRequest): HealthCheckResponse {
        val databaseHealth = checkDatabaseHealth()
        val togglzHealth = checkTogglzHealth()
        val overallStatus = if (databaseHealth.status == HealthStatus.HEALTH_STATUS_UP &&
            togglzHealth.status == HealthStatus.HEALTH_STATUS_UP) {
            HealthStatus.HEALTH_STATUS_UP
        } else {
            HealthStatus.HEALTH_STATUS_DOWN
        }

        return healthCheckResponse {
            status = overallStatus
            components.put("database", databaseHealth)
            components.put("featureFlags", togglzHealth)
        }
    }

    override suspend fun live(request: HealthCheckRequest): LivenessResponse {
        return livenessResponse {
            status = HealthStatus.HEALTH_STATUS_UP
        }
    }

    override suspend fun features(request: FeaturesRequest): FeaturesResponse {
        val flags = Features.entries.associate { feature ->
            feature.name to featureManager.isActive(feature)
        }
        return featuresResponse {
            features.putAll(flags)
        }
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
            componentHealth {
                status = HealthStatus.HEALTH_STATUS_UP
            }
        } catch (e: Exception) {
            componentHealth {
                status = HealthStatus.HEALTH_STATUS_DOWN
                error = e.message ?: "Unknown error"
            }
        }
    }

    private fun checkTogglzHealth(): ComponentHealth {
        return try {
            featureManager.features
            componentHealth {
                status = HealthStatus.HEALTH_STATUS_UP
            }
        } catch (e: Exception) {
            componentHealth {
                status = HealthStatus.HEALTH_STATUS_DOWN
                error = e.message ?: "Unknown error"
            }
        }
    }
}
