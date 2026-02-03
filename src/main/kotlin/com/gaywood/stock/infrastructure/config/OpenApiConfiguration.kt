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

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Stock Management API")
                .description("Bar & Kitchen Stock Management System - A DDD implementation for managing stock, orders, menus, and staff")
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
                    .description("Local Development Server")
            )
        )
}
