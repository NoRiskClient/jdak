import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    `maven-publish`
}

group = "gg.norisk"
version = "2.0.0"
description = "Powerful Discord bot Framework based on JDA for Kotlin"

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xjvm-default=all")
            freeCompilerArgs.add("-Xskip-prerelease-check")
            freeCompilerArgs.add("-Xcontext-receivers")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/") // jda ktx
}

dependencies {
    compileOnly(kotlin("stdlib", "2.2.0"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:2.2.0")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("net.dv8tion:JDA:5.6.0")
    compileOnly("com.github.minndevelopment:jda-ktx:0.12.0")
}

publishing {
    publications {
        create<MavenPublication>("binaryAndSources") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }

    repositories {
        fun MavenArtifactRepository.applyCredentials() = credentials {
            username =
                (System.getenv("NORISK_NEXUS_USERNAME") ?: project.findProperty("noriskMavenUsername")).toString()
            password =
                (System.getenv("NORISK_NEXUS_PASSWORD") ?: project.findProperty("noriskMavenPassword")).toString()
        }
        maven {
            name = "dev"
            url = uri("https://maven-test.norisk.gg/repository/maven-releases/")
            applyCredentials()
        }
    }
}
