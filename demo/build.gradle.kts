plugins {
    application
    `java-library`
    kotlin("jvm")
}

application {
    mainClassName = "org.araqnid.appstatus.demo.Main"
}

group = "org.araqnid"

val jettyVersion: String by rootProject.extra
val jacksonVersion: String by rootProject.extra
val resteasyVersion: String by rootProject.extra
val guiceVersion: String by rootProject.extra
val guavaVersion: String by rootProject.extra

val web by configurations.creating

repositories {
    jcenter()
}

configurations {
    "testCompile" {
        exclude(module = "logback-classic")
    }
}

dependencies {
    implementation(rootProject)
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-servlet:$jettyVersion")
    implementation("org.jboss.resteasy:resteasy-jaxrs:$resteasyVersion")
    implementation("org.jboss.resteasy:resteasy-guice:$resteasyVersion")
    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-guice:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.21")
    web(project(":ui", "web"))
}

tasks {
    "run"(JavaExec::class) {
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
