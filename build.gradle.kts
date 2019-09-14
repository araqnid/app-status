plugins {
    java
    kotlin("jvm") version "1.3.50"
    `maven-publish`
    `java-library`
    id("com.timgroup.webpack") version "1.0.50" apply false
    id("com.jfrog.bintray") version "1.8.4"
}

group = "org.araqnid"

allprojects {
    if (rootProject.hasProperty("version"))
        version = rootProject.property("version").toString()
}

val guavaVersion by extra("26.0-jre")
val jettyVersion by extra("9.4.12.v20180830")
val jacksonVersion by extra("2.9.6")
val resteasyVersion by extra("3.1.4.Final")
val guiceVersion by extra("4.2.1")

val web by configurations.creating

repositories {
    jcenter()
}

dependencies {
    api("com.google.inject:guice:$guiceVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:2.8.0")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation(kotlin("stdlib-jdk8", "1.2.71"))
    implementation(kotlin("reflect", "1.2.71"))
    testImplementation("junit:junit:4.12")
    testImplementation("com.natpryce:hamkrest:1.4.2.2")
    testImplementation("org.araqnid:hamkrest-json:1.0.3")
    testImplementation(kotlin("test-junit", "1.2.71"))
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
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
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
    if (version != Project.DEFAULT_VERSION) {
        pkg.version.name = version.toString()
        pkg.version.vcsTag = "v$version"
    }
}
