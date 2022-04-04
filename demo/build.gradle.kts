plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("org.araqnid.appstatus.demo.Main")
}

group = "org.araqnid"

val web by configurations.creating

configurations {
    "runtimeClasspath" {
        exclude(module = "logback-classic")
    }
}

dependencies {
    val slf4jVersion = "1.7.30"
    val jettyVersion = "9.4.26.v20200117"
    val resteasyVersion = "3.1.4.Final"
    val jacksonVersion = "2.13.2"
    implementation(project(":guice"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
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
    runtimeOnly("org.slf4j:slf4j-simple:$slf4jVersion")
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
