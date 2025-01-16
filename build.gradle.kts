val javaVersion = JavaLanguageVersion.of(21)
val springdocVersion = "2.8.3"
val tokenSupportVersion = "5.0.14"
val springCloudVersion = "4.2.0"
val commonVersion = "3.2025.01.14_14.19-79b3041cae56"
val mockkVersion = "1.13.16"
val testcontainerVersion = "1.19.0"
val okhVersion = "4.12.0"
val tokenValidationVersion = "1.3.0"
val oidcSupportVersion = "0.2.18"
val mockOAuth2ServerVersion = "2.1.10"

group = "no.nav.populasjonstrilgangskontroll"
version = "1.0.0"

plugins {
    application

    kotlin("jvm") version "2.1.0"
    id("com.diffplug.spotless") version "7.0.2"
    id("com.github.ben-manes.versions") version "0.51.0"

    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.1.0"

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
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")

    //Rest
    implementation("no.nav.common:rest:$commonVersion")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:$springCloudVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")
    testImplementation("no.nav.security:token-validation-spring-test:$tokenSupportVersion")
}


kotlin {
    jvmToolchain(javaVersion.asInt())

    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

application {
    mainClass.set("no.nav.tilgangsmaskin.populasjonstilgangskontroll.AppKt")
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