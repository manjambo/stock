plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    jacoco
}

group = "com.gaywood"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation(libs.bundles.spring.boot)

    // Jackson Kotlin support
    implementation(libs.jackson.module.kotlin)

    // Kotlin reflection (required for Spring)
    implementation(libs.kotlin.reflect)

    // Kotlin Coroutines
    implementation(libs.bundles.coroutines)

    // Flyway
    implementation(libs.bundles.flyway)

    // Database drivers
    runtimeOnly(libs.h2)
    runtimeOnly(libs.postgresql)

    // Testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.kotlinx.coroutines.test)
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    // JaCoCo agent configuration for JDK 22
    extensions.configure(JacocoTaskExtension::class) {
        excludes = listOf("java.*", "jdk.*", "sun.*")
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
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
