import org.springframework.boot.gradle.tasks.bundling.BootJar

val javaVersion = JavaLanguageVersion.of(24)
val springdocVersion = "2.8.6"
val tokenSupportVersion = "5.0.20"
val springCloudVersion = "4.2.1"
val mockkVersion = "1.13.17"
val mockOAuth2ServerVersion = "2.1.10"

group = "no.nav.tilgangsmaskin.populasjonstrilgangskontroll"
version = "1.0.1"

plugins {
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.25"
    kotlin("jvm") version "1.9.25"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.1.20"
    id("org.cyclonedx.bom") version "2.2.0"
    kotlin("plugin.jpa") version "1.9.25"
    id("com.google.cloud.tools.jib") version "3.4.5"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

allOpen {
    annotation("jakarta.persistence.Entity")
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
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("com.neovisionaries:nv-i18n:1.29")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework:spring-aspects")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.hibernate.orm:hibernate-micrometer")
    implementation("no.nav.boot:boot-conditionals:5.1.6")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    implementation("no.nav.security:token-client-spring:$tokenSupportVersion")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-database-postgresql")
    testImplementation("com.github.stefanbirkner:system-lambda:1.2.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:$springCloudVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")
    testImplementation("no.nav.security:token-validation-spring-test:$tokenSupportVersion")
    testImplementation(kotlin("test"))
    testImplementation("com.h2database:h2")
}


application {
    mainClass.set("no.nav.tilgangsmaskin.populasjonstilgangskontroll.AppKt")
}
tasks.withType<BootJar> {
    archiveFileName = "app.jar"
}

if (project.hasProperty("skipLint")) {
    gradle.startParameter.excludedTaskNames += "spotlessKotlinCheck"
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
jvmToolchain(24)

compilerOptions {
freeCompilerArgs.add("-Xjsr305=strict")
    }
}
