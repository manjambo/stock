package com.gaywood.stock.infrastructure.persistence

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
annotation class IntegrationTest

abstract class SpringBootTestSupport {
    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL" }
            registry.add("spring.datasource.driver-class-name") { "org.h2.Driver" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add("spring.flyway.enabled") { "false" }
        }
    }
}
