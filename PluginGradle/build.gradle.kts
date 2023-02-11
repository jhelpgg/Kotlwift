plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.5.30"
    `java-gradle-plugin`
}

val VERSION = "1.2.10"
val GROUP_ID = "fr.jhelp.compiler"

group = GROUP_ID
version = VERSION

repositories {
    mavenLocal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("compiler") {
            id = GROUP_ID
            implementationClass = "fr.jhelp.compiler.CompilePlugin"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
