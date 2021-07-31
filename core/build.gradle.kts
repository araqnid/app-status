import java.net.URI

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

project.description = "App status page model"

val web by configurations.creating

dependencies {
    api("com.fasterxml.jackson.core:jackson-annotations:${LibraryVersions.jackson}")
    api(kotlin("stdlib-jdk8"))
    implementation("com.google.guava:guava:${LibraryVersions.guava}")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.araqnid.kotlin.assert-that:assert-that:${LibraryVersions.assertThat}")
    testImplementation(kotlin("test-junit"))
    web(project(":ui", "web"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
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
            pom {
                name.set(project.name)
                description.set(project.description)
                licenses {
                    license {
                        name.set("Apache")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                url.set("https://github.com/araqnid/hamkrest-json")
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/araqnid/hamkrest-json/issues")
                }
                scm {
                    connection.set("https://github.com/araqnid/hamkrest-json.git")
                    url.set("https://github.com/araqnid/hamkrest-json")
                }
                developers {
                    developer {
                        name.set("Steven Haslam")
                        email.set("araqnid@gmail.com")
                    }
                }
            }
        }
    }

    repositories {
        val sonatypeUser: String? by project
        if (sonatypeUser != null) {
            maven {
                name = "OSSRH"
                url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                val sonatypePassword: String by project
                credentials {
                    username = sonatypeUser
                    password = sonatypePassword
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}
