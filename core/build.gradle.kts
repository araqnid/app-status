plugins {
    kotlin("jvm")
    `maven-publish`
    `java-library`
}

val web by configurations.creating

repositories {
    jcenter()
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-annotations:${LibraryVersions.jackson}")
    api(kotlin("stdlib-jdk8"))
    implementation("com.google.guava:guava:${LibraryVersions.guava}")
    testImplementation("junit:junit:4.13")
    testImplementation("org.araqnid.kotlin.assert-that:assert-that:${LibraryVersions.assertThat}")
    testImplementation(kotlin("test-junit"))
    web(project(":ui", "web"))
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
            freeCompilerArgs = listOf("-Xjvm-default=enable")
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
            artifactId = "app-status-core"
        }
    }

    repositories {
        maven(url = "https://maven.pkg.github.com/araqnid/app-status") {
            name = "github"
            credentials(githubUserCredentials(project))
        }
    }
}
