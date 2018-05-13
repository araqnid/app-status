import java.io.ByteArrayOutputStream

plugins {
    java
    kotlin("jvm") version "1.2.41"
    `maven-publish`
    `java-library`
    id("com.timgroup.webpack") version "1.0.37" apply false
    id("com.jfrog.bintray") version "1.7.3"
}

val gitVersion by extra {
    val capture = ByteArrayOutputStream()
    project.exec {
        commandLine("git", "describe", "--tags")
        standardOutput = capture
    }
    String(capture.toByteArray())
            .trim()
            .removePrefix("v")
            .replace('-', '.')
}

group = "org.araqnid"
version = gitVersion

val guavaVersion by extra("23.6-jre")
val jettyVersion by extra("9.4.8.v20171121")
val jacksonVersion by extra("2.9.3")
val resteasyVersion by extra("3.1.4.Final")
val guiceVersion by extra("4.1.0")

val web by configurations.creating

repositories {
    jcenter()
}

dependencies {
    api("com.google.inject:guice:$guiceVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:2.8.0")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation(kotlin("stdlib-jdk8", "1.2.10"))
    implementation(kotlin("reflect", "1.2.10"))
    testImplementation("junit:junit:4.12")
    testImplementation("com.natpryce:hamkrest:1.4.2.2")
    testImplementation("org.araqnid:hamkrest-json:1.0.3")
    testImplementation(kotlin("test-junit", "1.2.10"))
    web(project("ui", "web"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
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
            from(web)
        }
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

publishing {
    (publications) {
        "mavenJava"(MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}

bintray {
    user = (project.properties["bintray.user"] ?: "").toString()
    key = (project.properties["bintray.apiKey"] ?: "").toString()
    publish = true
    setPublications("mavenJava")
    pkg.repo = "maven"
    pkg.name = "app-status"
    pkg.setLicenses("Apache-2.0")
    pkg.vcsUrl = "https://github.com/araqnid/app-status"
    pkg.desc = "Expose health check results and some metadata on a single app page"
    pkg.version.name = gitVersion
    if (!gitVersion.contains(".g")) {
        pkg.version.vcsTag = "v$gitVersion"
    }
}
