package com.gaywood.stock.infrastructure.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.togglz.core.manager.FeatureManager
import org.togglz.core.repository.FeatureState

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TogglzSpec @Autowired constructor(
    private val mockMvc: MockMvc,
    private val featureManager: FeatureManager
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        test("FeatureManager should be properly configured") {
            featureManager.features.toList().map { it.name() } shouldContainAll listOf(
                "ORDER_NOTIFICATIONS",
                "LOW_STOCK_ALERTS",
                "KITCHEN_DISPLAY",
                "TABLE_RESERVATIONS",
                "LOYALTY_POINTS"
            )
        }

        test("LOW_STOCK_ALERTS should be enabled by default") {
            featureManager.isActive(Features.LOW_STOCK_ALERTS).shouldBeTrue()
        }

        test("ORDER_NOTIFICATIONS should be disabled by default") {
            featureManager.isActive(Features.ORDER_NOTIFICATIONS).shouldBeFalse()
        }

        test("KITCHEN_DISPLAY should be disabled by default") {
            featureManager.isActive(Features.KITCHEN_DISPLAY).shouldBeFalse()
        }

        test("TABLE_RESERVATIONS should be disabled by default") {
            featureManager.isActive(Features.TABLE_RESERVATIONS).shouldBeFalse()
        }

        test("LOYALTY_POINTS should be disabled by default") {
            featureManager.isActive(Features.LOYALTY_POINTS).shouldBeFalse()
        }

        test("features can be toggled on") {
            featureManager.setFeatureState(FeatureState(Features.KITCHEN_DISPLAY, true))

            featureManager.isActive(Features.KITCHEN_DISPLAY).shouldBeTrue()

            // Reset for other tests
            featureManager.setFeatureState(FeatureState(Features.KITCHEN_DISPLAY, false))
        }

        test("features can be toggled off") {
            featureManager.setFeatureState(FeatureState(Features.LOW_STOCK_ALERTS, false))

            featureManager.isActive(Features.LOW_STOCK_ALERTS).shouldBeFalse()

            // Reset for other tests
            featureManager.setFeatureState(FeatureState(Features.LOW_STOCK_ALERTS, true))
        }

        test("feature status can be checked via injected FeatureManager") {
            // Use the injected FeatureManager for reliable testing
            // Note: Features.isActive() relies on FeatureContext which may not be
            // configured in all test scenarios. Prefer injecting FeatureManager.
            featureManager.isActive(Features.LOW_STOCK_ALERTS).shouldBeTrue()
            featureManager.isActive(Features.ORDER_NOTIFICATIONS).shouldBeFalse()
        }

        test("feature state can be retrieved") {
            val state = featureManager.getFeatureState(Features.LOW_STOCK_ALERTS)

            state.feature.name() shouldBe "LOW_STOCK_ALERTS"
            state.isEnabled.shouldBeTrue()
        }

        test("/health/features endpoint returns all feature states") {
            mockMvc.perform(get("/health/features"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.features.ORDER_NOTIFICATIONS").value(false))
                .andExpect(jsonPath("$.features.LOW_STOCK_ALERTS").value(true))
                .andExpect(jsonPath("$.features.KITCHEN_DISPLAY").value(false))
                .andExpect(jsonPath("$.features.TABLE_RESERVATIONS").value(false))
                .andExpect(jsonPath("$.features.LOYALTY_POINTS").value(false))
        }

        test("/health/ready includes featureFlags component status") {
            mockMvc.perform(get("/health/ready"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.components.featureFlags.status").value("UP"))
        }

        test("toggling feature is reflected in /health/features endpoint") {
            // Enable a feature
            featureManager.setFeatureState(FeatureState(Features.TABLE_RESERVATIONS, true))

            mockMvc.perform(get("/health/features"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.features.TABLE_RESERVATIONS").value(true))

            // Reset for other tests
            featureManager.setFeatureState(FeatureState(Features.TABLE_RESERVATIONS, false))

            mockMvc.perform(get("/health/features"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.features.TABLE_RESERVATIONS").value(false))
        }
    }
}
