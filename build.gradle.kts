plugins {
    kotlin("jvm") version "1.3.61"
    `maven-publish`
    `java-library`
    id("com.timgroup.webpack") version "1.0.63" apply false
    id("com.jfrog.bintray") version "1.8.4"
}

allprojects {
    group = "org.araqnid.app-status"

    if (rootProject.hasProperty("version"))
        version = rootProject.property("version").toString()
}

LibraryVersions.toMap().forEach { (name, value) ->
    ext["${name}Version"] = value
}

val web by configurations.creating

repositories {
    jcenter()
}

dependencies {
    api("com.google.inject:guice:${LibraryVersions.guice}")
    api("com.fasterxml.jackson.core:jackson-annotations:${LibraryVersions.jackson}")
    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))
    implementation("com.google.guava:guava:${LibraryVersions.guava}")
    testImplementation("junit:junit:4.13")
    testImplementation("com.natpryce:hamkrest:${LibraryVersions.hamkrest}")
    testImplementation("org.araqnid:hamkrest-json:1.1.0")
    testImplementation(kotlin("test-junit"))
    web(project("ui", "web"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

tasks {
    withType<JavaCompile>().configureEach {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
        options.isIncremental = true
        options.isDeprecation = true
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    named("jar", Jar::class) {
        manifest {
            attributes["Implementation-Title"] = project.description ?: project.name
            attributes["Implementation-Version"] = project.version
        }
        into("org/araqnid/appstatus/site") {
            from(web)
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
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
