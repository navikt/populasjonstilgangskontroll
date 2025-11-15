import org.springframework.boot.gradle.tasks.bundling.BootJar

val javaVersion = JavaLanguageVersion.of(21)
val springdocVersion = "2.8.14"
val tokenSupportVersion = "3.0.0-SNAPSHOT"
val mockkVersion = "1.14.6"



group = "no.nav.tilgangsmaskin.populasjonstilgangskontroll"
version = "1.0.1"

plugins {
    val kotlinVersion = "2.2.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version "4.0.0-RC2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.cyclonedx.bom") version "3.0.0"
    id("com.google.cloud.tools.jib") version "3.4.5"
    application
}
springBoot {
    buildInfo()
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/navikt/token-support")
        credentials {
            username = findProperty("gpr.user") as String?
            password = findProperty("gpr.key") as String?
        }
    }
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
}

configurations.all {
    resolutionStrategy {
        //failOnNonReproducibleResolution()
    }
}

dependencies {
    implementation("org.apache.commons:commons-pool2:2.12.1")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.21.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.21.0-alpha")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing")
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("net.logstash.logback:logstash-logback-encoder:9.0")
    implementation("no.nav.boot:boot-conditionals:5.1.11")
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
    implementation("org.springframework.boot:spring-boot-starter-web"){
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-security")
    }

    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-webclient")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-jackson2")

    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework:spring-aspects")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    testImplementation("org.springframework.boot:spring-boot-micrometer-metrics-test")
    testImplementation("com.redis:testcontainers-redis")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage","junit-vintage-engine")
    }
    testImplementation("org.awaitility:awaitility-kotlin:4.3.0")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")
    testImplementation(kotlin("test"))
}

dependencyManagement {
    imports {
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.21.0")
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
