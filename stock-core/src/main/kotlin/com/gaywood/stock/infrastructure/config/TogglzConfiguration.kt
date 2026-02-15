package com.gaywood.stock.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.togglz.core.manager.EnumBasedFeatureProvider
import org.togglz.core.spi.FeatureProvider
import org.togglz.core.user.FeatureUser
import org.togglz.core.user.SimpleFeatureUser
import org.togglz.core.user.UserProvider

@Configuration
class TogglzConfiguration {

    @Bean
    fun featureProvider(): FeatureProvider =
        EnumBasedFeatureProvider(Features::class.java)

    @Bean
    fun featureUserProvider(): UserProvider = UserProvider {
        SimpleFeatureUser("admin", true)
    }
}
