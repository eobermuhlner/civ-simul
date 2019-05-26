import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    val kotlinVersion = "1.3.31"

    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "2.1.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}


group = "ch.obermuhlner"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io") {
        name = "jitpack"
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:1.7.19")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.github.kittinunf.fuel:fuel:2.1.0")
    implementation("com.github.kittinunf.fuel:fuel-gson:2.1.0")
    implementation("org.slf4j:slf4j-api:1.7.25")

    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
