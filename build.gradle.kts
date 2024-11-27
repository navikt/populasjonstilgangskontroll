val javaVersion = JavaLanguageVersion.of(21)
val springdocVersion = "2.7.0"
val tokenSupportVersion = "5.0.13"
val springCloudVersion = "4.1.5"
val commonVersion = "3.2024.11.26_16.35-432a29107830"
val mockkVersion = "1.13.13"
val testcontainerVersion = "1.19.0"
val okhVersion = "4.12.0"
val tokenValidationVersion = "1.3.0"
val oidcSupportVersion = "0.2.18"

group = "no.nav.populasjonstrilgangskontroll"
version = "1.0.0"

plugins {
    application

    kotlin("jvm") version "2.0.21"
    id("com.diffplug.spotless") version "6.25.0"
    id("com.github.ben-manes.versions") version "0.51.0"

    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("plugin.spring") version "2.0.21"

    id("org.cyclonedx.bom") version "1.10.0"
}

repositories {
    mavenCentral()
    mavenLocal()

    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
}

apply(plugin = "com.diffplug.spotless")

spotless {
    kotlin {
        ktlint("0.50.0")
    }
}

configurations.all {
    resolutionStrategy {
        failOnNonReproducibleResolution()
    }
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-common:$springdocVersion")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Logging
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("io.micrometer:micrometer-registry-prometheus")

    //httpclient
    implementation("com.squareup.okhttp3:okhttp:$okhVersion")


    //Token
    implementation("no.nav.common:token-client:$commonVersion")
    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    implementation("no.nav.security:oidc-spring-support:$oidcSupportVersion")
    implementation("no.nav.security:token-client-spring:$tokenSupportVersion")
    //implementation("no.nav.security:token-validation-jwt:$tokenValidationVersion")

    //Rest
    implementation("no.nav.common:rest:$commonVersion")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:$springCloudVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

    testImplementation("no.nav.security:token-validation-spring-test:$tokenSupportVersion")
}


kotlin {
    jvmToolchain(javaVersion.asInt())

    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

application {
    mainClass.set("no.nav.populasjonstilgangskontroll.Appkt")
}

if (project.hasProperty("skipLint")) {
    gradle.startParameter.excludedTaskNames += "spotlessKotlinCheck"
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName.set("app.jar")
}