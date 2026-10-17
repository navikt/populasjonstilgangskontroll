import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_26

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_26
    targetCompatibility = JavaVersion.VERSION_26
}

kotlin {
    compilerOptions {
        jvmTarget.set(JVM_26)
    }
}

repositories {
    mavenCentral()
}
