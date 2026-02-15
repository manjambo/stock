package com.gaywood.stock.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {

    companion object {
        const val UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    }

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Stock Management API")
                .description(
                    """
                    Bar & Kitchen Stock Management System - A DDD implementation for managing stock, orders, menus, and staff.

                    **Note:** Authentication should be configured before production deployment.
                    """.trimIndent()
                )
                .version("1.0.0")
                .contact(
                    Contact()
                        .name("Gaywood")
                        .url("https://github.com/manjambo/stock")
                )
                .license(
                    License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                )
        )
        .servers(
            listOf(
                Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server"),
                Server()
                    .url("https://api.gaywood.com")
                    .description("Production Server")
            )
        )
}
