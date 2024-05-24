import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.23"
}

group = "com.github.bea4dev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Minestom
    compileOnly("com.github.bea4dev:Minestom:c2dc171bc1")

    // VanillaSource
    compileOnly("com.github.bea4dev:VanillaSource:bde9b50560")

    // Noise
    implementation("de.articdive:jnoise-pipeline:4.1.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

tasks.jar {
    manifest {
        attributes("Multi-Release" to true)
    }

    from("src/main/resources") {
        include("**/*.*")
    }

    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}