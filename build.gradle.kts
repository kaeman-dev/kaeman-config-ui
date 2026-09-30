import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("net.fabricmc.fabric-loom")
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

val minecraftVersion = providers.gradleProperty("minecraft_version").get()
group = "cc.me0wo.kaeman"
version = "${providers.gradleProperty("library_version").get()}+$minecraftVersion"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.wispforest.io")
    maven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    api("io.wispforest:owo-lib:${property("owo_version")}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}
kotlin { compilerOptions.jvmTarget.set(JvmTarget.JVM_25) }
tasks.withType<JavaCompile>().configureEach { options.release.set(25) }
tasks.jar { from("LICENSE") { rename { "kaeman-config-ui-LICENSE" } } }
tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", minecraftVersion)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version, "minecraft_version" to minecraftVersion)
    }
}
publishing {
    publications { create<MavenPublication>("mavenJava") { from(components["java"]) } }
}
