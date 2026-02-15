plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    jacoco
}

dependencies {
    implementation(project(":stock-core"))
    implementation(project(":stock-grpc"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")

    // Database drivers
    runtimeOnly(libs.h2)
    runtimeOnly(libs.postgresql)

    // Testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.data:spring-data-commons")
    testImplementation(libs.jackson.module.kotlin)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.togglz.testing)
}

kotlin {
    jvmToolchain(22)
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
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

// Open classes for Spring proxying
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
