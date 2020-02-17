plugins {
    kotlin("jvm")
    `maven-publish`
    `java-library`
    id("com.jfrog.bintray")
}

repositories {
    jcenter()
}

dependencies {
    api(project(":core"))
    api("com.google.inject:guice:${LibraryVersions.guice}")
    implementation(kotlin("reflect"))
    implementation("com.google.guava:guava:${LibraryVersions.guava}")
    testImplementation("junit:junit:4.13")
    testImplementation("com.natpryce:hamkrest:${LibraryVersions.hamkrest}")
    testImplementation("org.araqnid:hamkrest-json:1.1.0")
    testImplementation(kotlin("test-junit"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

tasks {
    withType<JavaCompile>().configureEach {
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
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "app-status-guice"
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
