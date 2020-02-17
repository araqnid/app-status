plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "org.araqnid.appstatus.demo.Main"
}

group = "org.araqnid"

val web by configurations.creating

repositories {
    jcenter()
}

configurations {
    "runtimeClasspath" {
        exclude(module = "logback-classic")
    }
}

dependencies {
    implementation(project(":guice"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.slf4j:slf4j-api:${LibraryVersions.slf4j}")
    implementation("org.eclipse.jetty:jetty-server:${LibraryVersions.jetty}")
    implementation("org.eclipse.jetty:jetty-servlet:${LibraryVersions.jetty}")
    implementation("org.jboss.resteasy:resteasy-jaxrs:${LibraryVersions.resteasy}")
    implementation("org.jboss.resteasy:resteasy-guice:${LibraryVersions.resteasy}")
    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:${LibraryVersions.jackson}")
    implementation("com.fasterxml.jackson.module:jackson-module-guice:${LibraryVersions.jackson}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava:${LibraryVersions.jackson}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:${LibraryVersions.jackson}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${LibraryVersions.jackson}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${LibraryVersions.jackson}")
    runtimeOnly("org.slf4j:slf4j-simple:${LibraryVersions.slf4j}")
    web(project(":ui", "web"))
}

tasks {
    named("run", JavaExec::class) {
        dependsOn(web)
        val webDir = buildDir.resolve("web")

        environment("DOCUMENT_ROOT", webDir.toString())

        doFirst {
            sync {
                from(web)
                into(webDir)
            }
        }
    }
}
