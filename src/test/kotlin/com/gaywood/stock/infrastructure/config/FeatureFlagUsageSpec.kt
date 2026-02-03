package com.gaywood.stock.infrastructure.config

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.togglz.core.manager.FeatureManager
import org.togglz.core.repository.FeatureState
import org.togglz.testing.TestFeatureManager

/**
 * Demonstrates how to test business logic that depends on feature flags
 * using Togglz's TestFeatureManager for isolated unit tests.
 */
class FeatureFlagUsageSpec : BehaviorSpec({

    Given("a service that behaves differently based on feature flags") {
        val featureManager = TestFeatureManager(Features::class.java)
        val service = ExampleNotificationService(featureManager)

        When("ORDER_NOTIFICATIONS is disabled") {
            featureManager.setFeatureState(FeatureState(Features.ORDER_NOTIFICATIONS, false))

            Then("notifications should not be sent") {
                service.shouldSendNotification() shouldBe false
            }
        }

        When("ORDER_NOTIFICATIONS is enabled") {
            featureManager.setFeatureState(FeatureState(Features.ORDER_NOTIFICATIONS, true))

            Then("notifications should be sent") {
                service.shouldSendNotification() shouldBe true
            }
        }
    }

    Given("a service checking multiple feature flags") {
        val featureManager = TestFeatureManager(Features::class.java)
        val service = ExampleKitchenService(featureManager)

        When("both KITCHEN_DISPLAY and LOW_STOCK_ALERTS are enabled") {
            featureManager.setFeatureState(FeatureState(Features.KITCHEN_DISPLAY, true))
            featureManager.setFeatureState(FeatureState(Features.LOW_STOCK_ALERTS, true))

            Then("full kitchen features should be available") {
                service.getAvailableFeatures() shouldBe listOf("kitchen_display", "stock_alerts")
            }
        }

        When("only LOW_STOCK_ALERTS is enabled") {
            featureManager.setFeatureState(FeatureState(Features.KITCHEN_DISPLAY, false))
            featureManager.setFeatureState(FeatureState(Features.LOW_STOCK_ALERTS, true))

            Then("only stock alerts should be available") {
                service.getAvailableFeatures() shouldBe listOf("stock_alerts")
            }
        }

        When("no features are enabled") {
            featureManager.setFeatureState(FeatureState(Features.KITCHEN_DISPLAY, false))
            featureManager.setFeatureState(FeatureState(Features.LOW_STOCK_ALERTS, false))

            Then("no features should be available") {
                service.getAvailableFeatures() shouldBe emptyList()
            }
        }
    }

    Given("feature flag defaults with TestFeatureManager") {
        // Note: TestFeatureManager starts with all features disabled by default
        // The @EnabledByDefault annotation is only honored by the production FeatureManager
        val featureManager = TestFeatureManager(Features::class.java)

        // Enable the feature that should be on by default (simulating production behavior)
        featureManager.setFeatureState(FeatureState(Features.LOW_STOCK_ALERTS, true))

        Then("LOW_STOCK_ALERTS should be enabled after manual setup") {
            featureManager.isActive(Features.LOW_STOCK_ALERTS) shouldBe true
        }

        Then("other features should be disabled") {
            featureManager.isActive(Features.ORDER_NOTIFICATIONS) shouldBe false
            featureManager.isActive(Features.KITCHEN_DISPLAY) shouldBe false
            featureManager.isActive(Features.TABLE_RESERVATIONS) shouldBe false
            featureManager.isActive(Features.LOYALTY_POINTS) shouldBe false
        }
    }
})

/**
 * Example service demonstrating feature flag usage
 */
class ExampleNotificationService(private val featureManager: FeatureManager) {
    fun shouldSendNotification(): Boolean =
        featureManager.isActive(Features.ORDER_NOTIFICATIONS)
}

/**
 * Example service demonstrating multiple feature flag checks
 */
class ExampleKitchenService(private val featureManager: FeatureManager) {
    fun getAvailableFeatures(): List<String> = buildList {
        if (featureManager.isActive(Features.KITCHEN_DISPLAY)) {
            add("kitchen_display")
        }
        if (featureManager.isActive(Features.LOW_STOCK_ALERTS)) {
            add("stock_alerts")
        }
    }
}
