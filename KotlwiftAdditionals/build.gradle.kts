import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    application
}

val VERSION_PREFIX = "1.0.7"
val VERSION = VERSION_PREFIX
val GROUP_ID = "fr.jhelp.kotlwift"
val ARTIFACT_ID = "kotlwift"

group = GROUP_ID
version = VERSION

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    testImplementation(kotlin("test-junit5"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    applicationName = "fr.jhelp.kotlwift.MainKt"
}

