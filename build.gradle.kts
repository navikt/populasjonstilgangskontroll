import org.springframework.boot.gradle.tasks.bundling.BootJar

val javaVersion = JavaLanguageVersion.of(21)
val springdocVersion = "3.0.0"
val tokenSupportVersion = "6.0.0"
val mockkVersion = "1.14.6"
val kotestVersion = "6.0.5"
val otelVersion = "2.22.0"
val conditionalsVersion = "6.0.0"
val logstashVersion = "9.0"
val coroutinesVersion = "1.9.0"
val poolsVersion = "2.12.1"
val awaitilityVersion = "4.3.0"
val springMockkVersion = "4.0.2"


group = "no.nav.tilgangsmaskin.populasjonstilgangskontroll"
version = "1.0.1"

plugins {
    val kotlinVersion = "2.2.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.cyclonedx.bom") version "2.4.1"
    id("com.google.cloud.tools.jib") version "3.5.1"
    id("io.kotest") version "6.0.7"
    id("com.gorylenko.gradle-git-properties") version "2.5.4"
    application
}
springBoot {
    buildInfo {
        properties {
            additional = mapOf(
                "kotlin.version" to "2.2.20",
                "jdk.version" to javaVersion.asInt().toString(),
                "jdk.vendor" to System.getProperty("java.vendor")
            )
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
}

configurations.all {
    resolutionStrategy {
        failOnNonReproducibleResolution()
    }
}

dependencies {
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations")
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:$otelVersion-alpha")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("no.nav.boot:boot-conditionals:$conditionalsVersion")
    implementation("no.nav.security:token-client-spring:$tokenSupportVersion")
    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.apache.commons:commons-pool2:$poolsVersion")
    implementation("org.hibernate.orm:hibernate-micrometer")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.postgresql:postgresql")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-webclient")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework:spring-aspects")
    testImplementation("org.springframework.boot:spring-boot-micrometer-metrics-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("org.awaitility:awaitility-kotlin:$awaitilityVersion")
    testImplementation("com.redis:testcontainers-redis")
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation(kotlin("test"))
}

dependencyManagement {
    imports {
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:$otelVersion")
    }
}

application {
    mainClass.set("no.nav.tilgangsmaskin.populasjonstilgangskontroll.AppKt")
}
tasks.withType<BootJar> {
    archiveFileName = "app.jar"
}

tasks.test {
    jvmArgs("--add-opens", "java.base/java.util=ALL-UNNAMED")
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(javaVersion)
    }
}

kotlin {
    jvmToolchain(javaVersion.asInt())

    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}
