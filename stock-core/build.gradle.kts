plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.dependency.management)
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    // Spring Boot (without the boot plugin - just as libraries)
    implementation(libs.bundles.spring.boot)

    // Jackson Kotlin support
    implementation(libs.jackson.module.kotlin)

    // Kotlin reflection (required for Spring)
    implementation(libs.kotlin.reflect)

    // Kotlin Coroutines
    implementation(libs.bundles.coroutines)

    // Flyway
    implementation(libs.bundles.flyway)

    // Feature Flags
    implementation(libs.bundles.togglz)

    // OpenAPI / Swagger
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // Database drivers
    runtimeOnly(libs.h2)
    runtimeOnly(libs.postgresql)
}

kotlin {
    jvmToolchain(22)
}

// Open classes for Spring proxying
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
