import org.gradle.api.JavaVersion.toVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.Companion.fromTarget
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.lang.Runtime.getRuntime

val javaVersion = JavaLanguageVersion.of(27)
val jvmTargetVersion = 26

group = "no.nav.tilgangsmaskin.populasjonstilgangskontroll"
version = "1.0.1"

extra["jackson-bom.version"] = "3.2.2"
extra["jackson-2-bom.version"] = "2.22.1"

plugins {
    jacoco
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.cyclonedx)
    alias(libs.plugins.git.properties)
    alias(libs.plugins.kotest)
}

java {
    sourceCompatibility = toVersion(jvmTargetVersion)
    targetCompatibility = toVersion(jvmTargetVersion)

    toolchain {
        languageVersion.set(javaVersion)
    }
}

springBoot {
    buildInfo {
        properties {
            val javaMetadata = javaToolchains.launcherFor(java.toolchain).get().metadata
            additional = mapOf(
                "kotlin.version" to libs.versions.kotlin.get(),
                "jdk.version" to javaMetadata.languageVersion.asInt().toString(),
                "jdk.vendor" to javaMetadata.vendor,
                "spring-boot.version" to libs.versions.spring.boot.get(),
            )
        }
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven/") }
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
    maven {
        url = uri("https://maven.pkg.github.com/navikt/pdl")
        credentials {
            username = "x-access-token"
            password = providers.environmentVariable("READER_TOKEN").orElse(providers.gradleProperty("gpr.token")).orNull
        }
    }
}


dependencies {
    constraints {
        implementation("at.yawk.lz4:lz4-java:1.12.0") {
            because("Upgrade Kafka's transitive LZ4 dependency from 1.11.2 to 1.12.0")
        }
    }
    implementation(libs.sikkerhetstjenesten.lib)
    implementation(libs.contract.pdl.avro)
    implementation(libs.boot.conditionals)
    implementation(libs.bundles.observability)
    implementation(libs.slack)
    implementation(libs.commons.pool2)
    implementation(libs.confluent.kafka.avro.serializer)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.hibernate.micrometer)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.logbook.spring.boot.starter)
    implementation(libs.logstash.logback.encoder)
    implementation(libs.postgresql)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.graphql)
    implementation(libs.spring.boot.starter.jetty)
    implementation(libs.spring.boot.starter.kafka)
    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.restclient)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web) {
        exclude(group = "org.springframework.boot", "spring-boot-starter-tomcat")
    }
    implementation(libs.spring.boot.starter.mustache)
    implementation(libs.spring.boot.starter.webclient)
    implementation(libs.springdoc.openapi.webmvc.ui)
    testImplementation(libs.caffeine)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.spring.boot.micrometer.metrics.test)
    testImplementation(libs.testcontainers.redis)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.spring.boot.starter.data.jpa.test)
    testImplementation(libs.spring.boot.starter.data.redis.test)
    testImplementation(libs.spring.boot.starter.kafka.test)
    testImplementation(libs.spring.boot.starter.restclient.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.mock.oauth2.server)
    testImplementation(libs.springmockk)
}


dependencyManagement {
    imports {
        mavenBom(libs.testcontainers.bom.get().toString())
        mavenBom(libs.opentelemetry.instrumentation.bom.get().toString())
        mavenBom(libs.kotest.bom.get().toString())
    }
}


tasks.withType<BootJar>().configureEach {
    archiveFileName = "app.jar"
}

gitProperties {
    keys = listOf(
        "git.branch",
        "git.commit.id",
        "git.commit.id.abbrev",
        "git.commit.time",
        "git.dirty",
        "git.build.version",
    )
    dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX"
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    maxHeapSize = "4g"
    maxParallelForks = (getRuntime().availableProcessors() / 2).coerceIn(1, 2)
    jvmArgs =
        listOf(
            "--add-opens",
            "java.base/java.util=ALL-UNNAMED",
            "--enable-native-access=ALL-UNNAMED",
            "-Dkotlinx.coroutines.debug=off",
            "-Xshare:off",
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
    compilerOptions {
        jvmTarget.set(fromTarget(jvmTargetVersion.toString()))
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}
