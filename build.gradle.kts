import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

val javaVersion = JavaLanguageVersion.of(21)
val springdocVersion = "2.8.9"
val tokenSupportVersion = "5.0.34"
val mockkVersion = "1.14.5"

group = "no.nav.tilgangsmaskin.populasjonstrilgangskontroll"
version = "1.0.1"

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.5.4"
    id("org.cyclonedx.bom") version "2.3.1"
    id("com.google.cloud.tools.jib") version "3.4.5"
    application
}

apply(plugin = "io.spring.dependency-management")


repositories {
    mavenCentral()
    mavenLocal()

    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
}

dependencies {
    implementation(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.18.1"))
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.18.1-alpha")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing")
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("net.logstash.logback:logstash-logback-encoder:8.1")
    implementation("no.nav.boot:boot-conditionals:5.1.9")
    implementation("no.nav.security:token-client-spring:$tokenSupportVersion")
    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.hibernate.orm:hibernate-micrometer")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.postgresql:postgresql")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework:spring-aspects")
    testImplementation("com.github.ben-manes.caffeine:caffeine")
    testImplementation("com.github.stefanbirkner:system-lambda:1.2.1")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation(kotlin("test"))
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
    reports {
    }
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
