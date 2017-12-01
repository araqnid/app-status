import java.io.ByteArrayOutputStream

plugins {
    java
    kotlin("jvm") version "1.2.0"
    `maven-publish`
    id("com.timgroup.webpack") version "1.0.1" apply false
}

group = "org.araqnid"
version = (fun (): String {
    val capture = ByteArrayOutputStream()
    project.exec {
        commandLine("git", "describe", "--tags")
        standardOutput = capture
    }
    return String(capture.toByteArray())
            .trim()
            .removePrefix("v")
            .replace('-', '.')
})()

val guavaVersion by extra { "23.5-jre" }

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "1.9"
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
        options.isIncremental = true
        options.isDeprecation = true
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    "jar"(Jar::class) {
        manifest {
            attributes["Implementation-Title"] = project.description ?: project.name
            attributes["Implementation-Version"] = project.version
        }
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

publishing {
    repositories {
        maven(url = "https://repo.araqnid.org/maven/") {
            credentials {
                username = "repo-user"
                password = "repo-password"
            }
        }
    }
    (publications) {
        "mavenJava"(MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}

repositories {
    mavenCentral()
    maven(url = "https://repo.araqnid.org/maven/")
}

dependencies {
    compile("com.google.guava:guava:23.5-jre")
    compile("com.google.inject:guice:4.1.0")
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
    compile("com.fasterxml.jackson.core:jackson-annotations:2.8.0")
    testCompile("junit:junit:4.12")
    testCompile("org.hamcrest:hamcrest-library:1.3")
    testCompile(kotlin("test-junit"))
}