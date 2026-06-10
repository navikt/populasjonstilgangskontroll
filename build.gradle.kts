import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.buildinfo.BuildInfo
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.core.SpringVersion
import java.lang.System.getProperty

val javaVersion = JavaLanguageVersion.of(25)
val springdocVersion = "3.0.3"
val tokenSupportVersion = "6.0.7"
val mockkVersion = "1.14.9"
val kotestVersion = "6.1.11"
val otelVersion = "2.27.0"
val conditionalsVersion = "6.0.5"
val logstashVersion = "9.0"
val coroutinesVersion = "1.9.0"
val poolsVersion = "2.13.1"
val springMockkVersion = "5.0.1"
val confluentVersion = "8.2.0"


group = "no.nav.tilgangsmaskin.populasjonstilgangskontroll"
version = "1.0.1"

plugins {
    val kotlinVersion = "2.3.21"
    id("jacoco")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.cyclonedx.bom") version "3.2.4"
    id("io.kotest") version "6.1.11"
    id("com.gorylenko.gradle-git-properties") version "2.5.7"
    application
}
springBoot {
    buildInfo {
        properties {
            additional = mapOf(
                "kotlin.version" to "2.3.21",
                "jdk.version" to javaVersion.asInt().toString(),
                "jdk.vendor" to getProperty("java.vendor"),
                "spring-boot.version" to plugins.getPlugin(SpringBootPlugin::class).javaClass.`package`.implementationVersion
            )
        }
    }
}

tasks.named<BuildInfo>("bootBuildInfo") {
    properties {
        additional.put("spring.version", provider {
            configurations.runtimeClasspath.get().resolvedConfiguration.resolvedArtifacts
                .firstOrNull { it.moduleVersion.id.group == "org.springframework" && it.moduleVersion.id.name == "spring-core" }
                ?.moduleVersion?.id?.version ?: "unknown"
        })
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

configurations.all {
    resolutionStrategy {
        failOnNonReproducibleResolution()
        eachDependency {  // TODO rethink on next boot version
            if (requested.group == "org.postgresql" && requested.name == "postgresql") {
                val minimum = "42.7.11"
                if ((requested.version ?: "0") < minimum) {
                    useVersion(minimum)
                    because("CVE-2025-49146, CVE-2026-42198 — minimum $minimum required")
                }
            }
        }
    }
}

dependencies {
    implementation("io.confluent:kafka-avro-serializer:$confluentVersion") {
        exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
    }
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations")
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:$otelVersion-alpha")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("no.nav.boot:boot-conditionals:$conditionalsVersion")
    implementation("no.nav.security:token-client-spring:$tokenSupportVersion")
    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.apache.commons:commons-pool2:$poolsVersion")
    implementation("org.hibernate.orm:hibernate-micrometer")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.postgresql:postgresql")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-webclient")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework:spring-aspects")
    testImplementation("org.springframework.boot:spring-boot-micrometer-metrics-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage",  "junit-vintage-engine")
    }
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("com.redis:testcontainers-redis")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")
    testImplementation("org.springframework.boot:spring-boot-starter-kafka-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.kotest:kotest-extensions-spring:$kotestVersion")
}


dependencyManagement {
    imports {
        mavenBom("io.netty:netty-bom:4.2.13.Final")  // TODO rethink on next boot version
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:$otelVersion")
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
