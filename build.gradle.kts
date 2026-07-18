import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.gradle.api.file.DuplicatesStrategy.EXCLUDE
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_26
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.io.ByteArrayOutputStream
import java.lang.System.getProperty

val javaVersion = JavaLanguageVersion.of(26)

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
    alias(libs.plugins.asciidoctor)
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
    // Force newer jackson-databind version (required by gradle-avro-plugin)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.0")

    implementation(libs.confluent.kafka.avro.serializer) {
        exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
    }
    implementation(libs.slack.api)
    implementation(libs.slack.api.kotlin)
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
    testImplementation(libs.spring.restdocs.mockmvc)
}


dependencyManagement {
    imports {
        mavenBom(libs.opentelemetry.instrumentation.bom.get().toString())
        mavenBom(libs.kotest.bom.get().toString())
    }
}

application {
    mainClass.set("no.nav.tilgangsmaskin.AppKt")
}
tasks.withType<BootJar> {
    archiveFileName = "app.jar"
    duplicatesStrategy = EXCLUDE
}

tasks.withType<Jar> {
    duplicatesStrategy = EXCLUDE
}

java {
    toolchain {
        languageVersion.set(javaVersion)
    }
}

val generateGitProperties = tasks.register("generateGitProperties") {
    val outputFile = layout.buildDirectory.file("resources/main/git.properties")
    outputs.file(outputFile)

    fun git(vararg args: String) = providers.exec {
        commandLine("git", *args)
        isIgnoreExitValue = true
    }.standardOutput.asText.map { it.trim() }

    val branch      = git("rev-parse", "--abbrev-ref", "HEAD")
    val commitId    = git("rev-parse", "HEAD")
    val commitShort = git("rev-parse", "--short", "HEAD")
    val commitTime  = git("log", "-1", "--format=%cI")
    val dirty       = git("status", "--porcelain").map { it.isNotEmpty() }

    doLast {
        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(buildString {
                appendLine("git.branch=${branch.get()}")
                appendLine("git.commit.id=${commitId.get()}")
                appendLine("git.commit.id.abbrev=${commitShort.get()}")
                appendLine("git.commit.time=${commitTime.get()}")
                appendLine("git.dirty=${dirty.get()}")
                appendLine("git.build.version=${project.version}")
            })
        }
    }
}

tasks.named("processResources") {
    dependsOn(generateGitProperties)
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    doFirst {
        delete(layout.buildDirectory.dir("generated-snippets"))
        delete(layout.buildDirectory.dir("generated-restdocs-index"))
    }

    maxHeapSize = "4g"
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    jvmArgs =
        listOf(
            "--add-opens",
            "java.base/java.util=ALL-UNNAMED",
            "--enable-native-access=ALL-UNNAMED",
            "-Dkotlinx.coroutines.debug=off",
            "-Xshare:off",
        )
}

val generateRestDocsIndex = tasks.register<GenerateRestDocsIndexTask>("generateRestDocsIndex") {
    description = "Generates index.adoc from REST Docs snippets"
    dependsOn(tasks.test)
    snippetsDir = layout.buildDirectory.dir("generated-snippets")
    outputDir = layout.buildDirectory.dir("generated-restdocs-index")
    openApiPropertiesFile = layout.projectDirectory.file("src/main/resources/openapi/prod/tilgang.properties")
    tilgangControllerFile = layout.projectDirectory.file("src/main/kotlin/no/nav/tilgangsmaskin/tilgang/TilgangController.kt")
}

tasks.named("asciidoctor") {
    dependsOn(generateRestDocsIndex)
    inputs.dir(layout.buildDirectory.dir("generated-snippets"))
}

tasks.withType<AsciidoctorTask> {
    sourceDir(layout.buildDirectory.dir("generated-restdocs-index"))
    baseDirFollowsSourceDir()
    sources {
        include("index.adoc")
    }
    attributes(
        mapOf(
            "snippets" to layout.buildDirectory.dir("generated-snippets").get().asFile.absolutePath,
            "source-highlighter" to "highlight.js",
            "toc" to "left",
            "toclevels" to "3",
            "sectlinks" to "",
        )
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
        jvmTarget.set(JVM_26)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}
