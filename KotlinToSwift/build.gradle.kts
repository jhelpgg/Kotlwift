import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30"
plugins {
    kotlin("jvm") version "1.5.30"
    application
}
group = "fr.jhelp.kotlinToSwift"
version = "1.1.10"

repositories {
    mavenCentral()
}
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.value("fr.jhelp.kotlinToSwift.ToSwiftKt")
    fun getMainClass() : Property<String> {
        return mainClass.value("fr.jhelp.kotlinToSwift.ToSwiftKt")
    }
    //mainClassName = "fr.jhelp.kotlinToSwift.ToSwiftKt"
}

