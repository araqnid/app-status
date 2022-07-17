import java.net.URI

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

project.description = "Guice add-on for building status components with dependencies injected"

dependencies {
    api(project(":core"))
    api("com.google.inject:guice:5.1.0")
    implementation(kotlin("reflect"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.araqnid.kotlin.assert-that:assert-that:0.1.1")
    testImplementation(kotlin("test-junit"))
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
            pom {
                name.set(project.name)
                description.set(project.description)
                licenses {
                    license {
                        name.set("Apache")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                url.set("https://github.com/araqnid/app-status")
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/araqnid/app-status/issues")
                }
                scm {
                    connection.set("https://github.com/araqnid/app-status.git")
                    url.set("https://github.com/araqnid/app-status")
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
