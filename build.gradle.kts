import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    application
}

group = "me.modulariz"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    implementation("mysql:mysql-connector-java:8.0.11")
    implementation("org.ktorm:ktorm-core:3.2.0")
    implementation("org.ktorm:ktorm-support-mysql:3.2.0")
    implementation("io.ktor:ktor-serialization:1.6.4")
    implementation("com.google.code.gson:gson:2.8.9")
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-netty:1.6.0")
    implementation("io.ktor:ktor-html-builder:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("ServerKt")
}