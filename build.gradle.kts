import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.lang.System.getProperty
import kotlin.text.Charsets.UTF_8

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
}

java {
    toolchain {
        languageVersion.set(javaVersion)
    }
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
            "-Dkotlinx.coroutines.debug=off",
        )
}

val generateRestDocsIndex by tasks.registering {
    description = "Generates index.adoc from REST Docs snippets"
    dependsOn(tasks.test)

    val snippetsDir = layout.buildDirectory.dir("generated-snippets")
    val outputDir = layout.buildDirectory.dir("generated-restdocs-index")

    inputs.dir(snippetsDir)
    outputs.dir(outputDir)

    doLast {
        val snippets = snippetsDir.get().asFile
        if (!snippets.isDirectory) return@doLast

        val dirs = snippets.listFiles { f -> f.isDirectory }
            ?.map { it.name }
            ?.sorted()
            ?: return@doLast

        val grouped = dirs.groupBy { it.substringBefore("-") }

        val sb = StringBuilder()
        val sharedProblemDetailSnippet = dirs.firstOrNull {
            snippets.resolve(it).resolve("response-fields.adoc").exists()
        }
        val sharedProblemDetailContent = sharedProblemDetailSnippet?.let {
            snippets.resolve(it).resolve("response-fields.adoc").readText(UTF_8)
        }

        // Load descriptions from openapi properties
        val propsFile = file("src/main/resources/openapi-prod-tilgang.properties")
        val properties = mutableMapOf<String, String>()
        if (propsFile.exists()) {
            propsFile.readLines().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                    val parts = trimmed.split("=", limit = 2)
                    if (parts.size == 2) {
                        properties[parts[0]] = parts[1]
                    }
                }
            }
        }

        // Parse constants from TilgangController.kt
        val controllerFile = file("src/main/kotlin/no/nav/tilgangsmaskin/tilgang/TilgangController.kt")
        val controllerConstants = mutableMapOf<String, String>()
        if (controllerFile.exists()) {
            val content = controllerFile.readText(UTF_8)
            // Match constants that may span multiple lines - use [\s\n] to include newlines
            val pattern = """private const val (\w+)\s*=\s*"([^"]+)"""".toRegex()
            pattern.findAll(content.replace("\n", " ")).forEach { match ->
                val (name, value) = match.destructured
                controllerConstants[name] = value.trim()
            }
        }

        // Map snippet names to property keys using constants from TilgangController
        val endpointDescriptionKeys: Map<String, String> = mapOf(
            "obo-komplett" to (controllerConstants["SUMMARY_KOMPLETT_OBO"] ?: "openapi.tilgang.komplett.obo.summary"),
            "obo-kjerne" to (controllerConstants["SUMMARY_KJERNE_OBO"] ?: "openapi.tilgang.kjerne.obo.summary"),
            "obo-enkelttilgang" to (controllerConstants["SUMMARY_OVERSTYR"] ?: "openapi.tilgang.overstyr.summary"),
            "obo-bulk" to (controllerConstants["SUMMARY_BULK"] ?: "openapi.tilgang.bulk.summary"),
            "obo-bulk-regeltype" to (controllerConstants["DESCRIPTION_BULK_OBO_REGELTYPE"] ?: "openapi.tilgang.bulk.obo.regeltype.description"),
            "ccf-komplett" to (controllerConstants["SUMMARY_KOMPLETT_CCF"] ?: "openapi.tilgang.komplett.ccf.summary"),
            "ccf-kjerne" to (controllerConstants["SUMMARY_KJERNE_CCF"] ?: "openapi.tilgang.kjerne.ccf.summary"),
            "ccf-bulk" to (controllerConstants["SUMMARY_BULK"] ?: "openapi.tilgang.bulk.summary"),
            "ccf-bulk-regeltype" to (controllerConstants["DESCRIPTION_BULK_CCF_REGELTYPE"] ?: "openapi.tilgang.bulk.ccf.regeltype.description")
        )

        fun sectionTitle(name: String, prefix: String) =
            name.substringAfter("$prefix-")
                .replace("-", " ")
                .replaceFirstChar { it.uppercase() }

        fun docsTitle(name: String, prefix: String): String {
            val title = sectionTitle(name, prefix)
            return if (name.contains("-overstyr")) {
                title.replaceFirst("Overstyr", "Enkelttilgang")
            } else {
                title
            }
        }

        fun getDescription(name: String): String {
            val key = endpointDescriptionKeys[name]
            return if (key != null) {
                val normalizedKey = key.removePrefix("msg:")
                properties[normalizedKey] ?: properties[key] ?: key
            } else {
                docsTitle(name, name.substringBefore("-"))
            }
        }

        fun appendSnippetIncludes(name: String) {
            sb.appendLine("include::{snippets}/$name/http-request.adoc[]")
            sb.appendLine("include::{snippets}/$name/http-response.adoc[]")
            val responseFields = snippets.resolve(name).resolve("response-fields.adoc")
            if (responseFields.exists() && responseFields.readText(UTF_8) != sharedProblemDetailContent) {
                sb.appendLine()
                sb.appendLine(".Response fields")
                sb.appendLine("include::{snippets}/$name/response-fields.adoc[]")
            }
            sb.appendLine()
        }

        sb.appendLine("= Populasjonstilgangskontroll API")
        sb.appendLine(":doctype: book")
        sb.appendLine(":icons: font")
        sb.appendLine(":toc: left")
        sb.appendLine(":toclevels: 3")
        sb.appendLine(":sectlinks:")
        sb.appendLine()
        sb.appendLine("== Oversikt")
        sb.appendLine()
        sb.appendLine("REST API-dokumentasjon for Populasjonstilgangskontroll (Tilgangsmaskinen).")
        sb.appendLine()
        sb.appendLine("Tjenesten avgjør om en Nav-ansatt har tilgang til en bruker")
        sb.appendLine()
        if (sharedProblemDetailSnippet != null) {
            sb.appendLine("=== Felles ProblemDetail-felter for enkeltoppslag og enkelttilgang")
            sb.appendLine()
            sb.appendLine("include::{snippets}/$sharedProblemDetailSnippet/response-fields.adoc[]")
            sb.appendLine()
        }

        for ((prefix, names) in grouped) {
            val heading = when (prefix) {
                "obo" -> "On-Behalf-Of flow"
                "ccf" -> "Client Credentials Flow"
                else -> prefix.uppercase()
            }
            sb.appendLine("== $heading")
            sb.appendLine()

            val sortedNames = names.sorted()
            val overstyrRoot = "$prefix-enkelttilgang"
            val overstyrRelated = sortedNames.filter { it == overstyrRoot || it.startsWith("$overstyrRoot-") }
            val remaining = sortedNames.filterNot { it in overstyrRelated }

            for (name in remaining) {
                val title = getDescription(name)
                sb.appendLine("=== $title")
                sb.appendLine()
                appendSnippetIncludes(name)
            }

            if (overstyrRelated.isNotEmpty()) {
                sb.appendLine("=== Enkelttilgang")
                sb.appendLine()

                if (overstyrRelated.contains(overstyrRoot)) {
                    appendSnippetIncludes(overstyrRoot)
                }

                val alternatives = overstyrRelated.filter {
                    it != overstyrRoot && (
                        it.contains("begrunnelse-for-kort") ||
                            it.contains("uten-token")
                        )
                }
                if (alternatives.isNotEmpty()) {
                    sb.appendLine("==== Alternative responser")
                    sb.appendLine()
                    for (name in alternatives) {
                        val altTitle = getDescription(name)
                        sb.appendLine("===== $altTitle")
                        sb.appendLine()
                        sb.appendLine("include::{snippets}/$name/http-request.adoc[]")
                        sb.appendLine("include::{snippets}/$name/http-response.adoc[]")
                        sb.appendLine()
                    }
                }
            }
        }

        val outDir = outputDir.get().asFile
        outDir.mkdirs()
        outDir.resolve("index.adoc").writeText(sb.toString(), UTF_8)
    }
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
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}
