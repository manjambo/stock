plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.spring.dependency.management)
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation(project(":stock-core"))

    // gRPC
    implementation(libs.bundles.grpc)
    implementation(libs.grpc.spring.boot.starter)

    // Kotlin Coroutines for gRPC Kotlin
    implementation(libs.kotlinx.coroutines.core)

    // Kotlin reflection
    implementation(libs.kotlin.reflect)

    // Spring Data for Page/Pageable
    implementation("org.springframework.data:spring-data-commons")

    // Togglz for feature flags (needed by HealthGrpcService)
    implementation(libs.togglz.spring.boot.starter)
}

kotlin {
    jvmToolchain(22)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${libs.versions.grpcKotlin.get()}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
                create("grpckt")
            }
            task.builtins {
                create("kotlin")
            }
        }
    }
}

// Add generated sources to source sets
sourceSets {
    main {
        java {
            srcDirs(
                "${layout.buildDirectory.get()}/generated/source/proto/main/grpc",
                "${layout.buildDirectory.get()}/generated/source/proto/main/java",
                "${layout.buildDirectory.get()}/generated/source/proto/main/kotlin",
                "${layout.buildDirectory.get()}/generated/source/proto/main/grpckt"
            )
        }
    }
}
