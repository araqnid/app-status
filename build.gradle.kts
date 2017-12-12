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
        into("org/araqnid/appstatus/site") {
            from("ui/build/site")
        }
        dependsOn(":ui:webpack")
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
    jcenter()
    maven(url = "https://dl.bintray.com/araqnid/maven")
}

dependencies {
    compile("com.google.guava:guava:23.5-jre")
    compile("com.google.inject:guice:4.1.0")
    compile("com.fasterxml.jackson.core:jackson-annotations:2.8.0")
    implementation(kotlin("stdlib-jdk8", "1.2.0"))
    implementation(kotlin("reflect", "1.2.0"))
    testCompile("junit:junit:4.12")
    testCompile("com.natpryce:hamkrest:1.4.2.2")
    testCompile("org.araqnid:hamkrest-json:1.0.3")
    testCompile(kotlin("test-junit", "1.2.0"))
}
