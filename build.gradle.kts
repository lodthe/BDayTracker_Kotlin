import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.41"
    id("application")
}

application {
    mainClassName = "me.lodthe.bdaytracker.MainKt"
}

group = "me.lodthe"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

    implementation("org.litote.kmongo:kmongo-coroutine:3.11.2")

    implementation("org.kodein.di:kodein-di-generic-jvm:6.5.0")

    implementation("com.github.pengrad:java-telegram-bot-api:4.4.0")
    implementation("com.vk.api:sdk:1.0.2")

    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", "2.8.2")
    implementation("org.apache.logging.log4j", "log4j-api", "2.8.2")
    implementation("org.apache.logging.log4j", "log4j-core", "2.8.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.InternalCoroutinesApi"
}