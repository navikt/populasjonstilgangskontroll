import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.lang.System.getProperty

val javaVersion = JavaLanguageVersion.of(25)

group = "no.nav.tilgangsmaskin.populasjonstilgangskontroll"
version = "1.0.1"

plugins {
    id("jacoco")
    alias(libs.plugins.avro)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.cyclonedx)
    alias(libs.plugins.kotest)
    alias(libs.plugins.git.properties)
    application
}
springBoot {
    buildInfo {
        properties {
            additional = mapOf(
                "kotlin.version" to libs.versions.kotlin.get(),
                "jdk.version" to javaVersion.asInt().toString(),
                "jdk.vendor" to getProperty("java.vendor"),
                "spring-boot.version" to libs.versions.spring.boot.get(),
            )
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://packages.confluent.io/maven/") }
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
}


dependencies {
    implementation(libs.confluent.kafka.avro.serializer) {
        exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
    }
    implementation(libs.opentelemetry.instrumentation.annotations)
    implementation(libs.opentelemetry.logback.mdc)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.logstash.logback.encoder)
    implementation(libs.boot.conditionals)
    implementation(libs.token.client.spring)
    implementation(libs.token.validation.spring)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.commons.pool2)
    implementation(libs.hibernate.micrometer)
    implementation(libs.kotlin.reflect)
    implementation(libs.postgresql)
    implementation(libs.springdoc.openapi.webmvc.ui)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.caffeine)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.graphql)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web) {
        exclude(group = "org.springframework.boot", "spring-boot-starter-tomcat")
    }
    implementation(libs.spring.boot.starter.jetty)
    implementation(libs.spring.boot.starter.webclient)
    implementation(libs.spring.boot.starter.kafka)
    implementation(libs.spring.aspects)
    testImplementation(libs.spring.boot.micrometer.metrics.test)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.redis)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.spring.boot.starter.data.redis.test)
    testImplementation(libs.spring.boot.starter.kafka.test)
    testImplementation(libs.spring.boot.starter.restclient.test)
    testImplementation(libs.spring.boot.starter.data.jpa.test)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.springmockk)
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.extensions.spring)
}


dependencyManagement {
    imports {
        mavenBom(libs.opentelemetry.instrumentation.bom.get().toString())
    }
}

application {
    mainClass.set("no.nav.tilgangsmaskin.AppKt")
}
tasks.withType<BootJar> {
    archiveFileName = "app.jar"
}

java {
    toolchain {
        languageVersion.set(javaVersion)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    maxHeapSize = "4g"
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    jvmArgs =
        listOf(
            "--add-opens",
            "java.base/java.util=ALL-UNNAMED",
            "-Dkotlinx.coroutines.debug=off",
        )
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/tilgang/*Swagger*.class", "**/tilgang/dev/*.class")
            }
        })
    )
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}
kotlin {
    jvmToolchain(javaVersion.asInt())

    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}
