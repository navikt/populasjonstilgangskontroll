import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.gradle.api.file.DuplicatesStrategy.EXCLUDE
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_26
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.lang.System.getProperty

val javaVersion = JavaLanguageVersion.of(26)

group = "no.nav.tilgangsmaskin.populasjonstilgangskontroll"
extra["netty.version"] = "4.2.16.Final"  // TODO Midlertidig
extra["jackson3.version"] = "3.2.0"       // TODO Midlertidig

version = "1.0.1"
val buildVersionValue = version.toString()

plugins {
    jacoco
    alias(libs.plugins.avro)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.cyclonedx)
    alias(libs.plugins.kotest)
    alias(libs.plugins.asciidoctor)
}

allOpen {
    annotation("org.springframework.boot.test.context.SpringBootTest")
    annotation("org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest")
    annotation("org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest")
    annotation("org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest")
    annotation("org.springframework.boot.test.autoconfigure.web.client.RestClientTest")
    annotation("org.springframework.test.context.ContextConfiguration")
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
    //implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")

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
    implementation(libs.spring.boot.starter.webclient)
    implementation(libs.springdoc.openapi.webmvc.ui)
    testImplementation(libs.caffeine)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.spring.boot.micrometer.metrics.test)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.spring.boot.starter.data.jpa.test)
    testImplementation(libs.spring.boot.starter.data.redis.test)
    testImplementation(libs.spring.boot.starter.kafka.test)
    testImplementation(libs.spring.boot.starter.restclient.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.mock.oauth2.server)
    testImplementation(libs.spring.restdocs.mockmvc)
    testImplementation(libs.springmockk)
}


dependencyManagement {
    imports {
        mavenBom(libs.opentelemetry.instrumentation.bom.get().toString())
        mavenBom(libs.kotest.bom.get().toString())
    }
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

val generateGitProperties = tasks.register<GenerateGitPropertiesTask>("generateGitProperties") {
    description = "Generates git.properties file with Git metadata"
    outputFile = layout.buildDirectory.file("resources/main/git.properties")
    buildVersion = buildVersionValue
}

val cleanGeneratedSnippets = tasks.register<Delete>("cleanGeneratedSnippets") {
    delete(layout.buildDirectory.dir("generated-snippets"))
}

val cleanGeneratedRestDocsIndex = tasks.register<Delete>("cleanGeneratedRestDocsIndex") {
    delete(layout.buildDirectory.dir("generated-restdocs-index"))
}

tasks.named("processResources") {
    dependsOn(generateGitProperties)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    dependsOn(cleanGeneratedSnippets, cleanGeneratedRestDocsIndex)

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
