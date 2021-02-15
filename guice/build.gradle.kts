plugins {
    kotlin("jvm")
    `maven-publish`
    `java-library`
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
    testImplementation("org.araqnid.kotlin.assert-that:assert-that:${LibraryVersions.assertThat}")
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
