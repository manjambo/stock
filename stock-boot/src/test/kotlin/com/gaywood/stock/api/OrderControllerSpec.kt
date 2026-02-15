package com.gaywood.stock.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.gaywood.stock.api.dto.CreateOrderRequest
import com.gaywood.stock.api.dto.OrderItemRequest
import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.domain.menu.repository.MenuRepository
import com.gaywood.stock.domain.staff.repository.StaffRepository
import com.gaywood.stock.fixtures.MenuFixtures
import com.gaywood.stock.fixtures.StaffFixtures
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerSpec @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val staffRepository: StaffRepository,
    private val menuRepository: MenuRepository
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        test("placing an order via the API creates order successfully") {
            // Setup test data
            val staff = StaffFixtures.barWorker()
            staffRepository.save(staff)

            val ginItemId = MenuItemId.generate()
            val tonicItemId = MenuItemId.generate()
            val fishAndChipsItemId = MenuItemId.generate()

            val barMenu = MenuFixtures.barMenu(
                items = listOf(
                    MenuFixtures.houseGin(id = ginItemId),
                    MenuFixtures.tonicWater(id = tonicItemId)
                )
            )
            val foodMenu = MenuFixtures.foodMenu(
                items = listOf(
                    MenuFixtures.fishAndChips(id = fishAndChipsItemId)
                )
            )
            menuRepository.save(barMenu)
            menuRepository.save(foodMenu)

            val request = CreateOrderRequest(
                staffId = staff.id.value,
                tableNumber = 5,
                items = listOf(
                    OrderItemRequest(ginItemId.value, 2),
                    OrderItemRequest(tonicItemId.value, 2),
                    OrderItemRequest(fishAndChipsItemId.value, 2)
                )
            )

            val result = mockMvc.perform(
                post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isCreated)
                .andReturn()

            val body = result.response.contentAsString
            body shouldContain "PENDING"
        }

        test("requesting a non-existent order returns 404") {
            mockMvc.perform(get("/orders/non-existent-id"))
                .andExpect(status().isNotFound)
        }

        test("health endpoint returns UP with database status") {
            mockMvc.perform(get("/health"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.database.status").value("UP"))
        }

        test("readiness endpoint returns UP with database and feature flags status") {
            mockMvc.perform(get("/health/ready"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.database.status").value("UP"))
                .andExpect(jsonPath("$.components.featureFlags.status").value("UP"))
        }

        test("liveness endpoint returns UP") {
            mockMvc.perform(get("/health/live"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("UP"))
        }

        test("features endpoint returns feature flags status") {
            mockMvc.perform(get("/health/features"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.features.LOW_STOCK_ALERTS").value(true))
                .andExpect(jsonPath("$.features.ORDER_NOTIFICATIONS").exists())
        }
    }
}
