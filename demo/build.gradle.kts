import java.io.ByteArrayOutputStream

plugins {
    application
    java
}

apply {
    plugin("org.jetbrains.kotlin.jvm")
}

application {
    mainClassName = "org.araqnid.appstatus.demo.Main"
}

group = "org.araqnid"
version = (fun (): String {
    val capture = ByteArrayOutputStream()
    project.exec {
        commandLine("git", "describe", "--tags")
        standardOutput = capture
    }
    return String(capture.toByteArray())
            .trim()
            .removePrefix("v")
            .replace('-', '.')
})()

val jettyVersion by extra { "9.4.7.v20170914" }
val jacksonVersion by extra { "2.9.2" }
val resteasyVersion by extra { "3.1.4.Final" }
val guiceVersion by extra { "4.1.0" }
val guavaVersion by extra { "23.0" }

repositories {
    mavenCentral()
}

//configurations {
//    testCompile.exclude module: 'logback-classic'
//}

dependencies {
    compile(rootProject)
    implementation(kotlin("stdlib-jdk8", "1.2.0"))
    implementation(kotlin("reflect", "1.2.0"))
    compile("org.slf4j:slf4j-api:1.7.25")
    compile("org.eclipse.jetty:jetty-server:$jettyVersion")
    compile("org.eclipse.jetty:jetty-servlet:$jettyVersion")
    compile("org.jboss.resteasy:resteasy-jaxrs:$resteasyVersion")
    compile("org.jboss.resteasy:resteasy-guice:$resteasyVersion")
    compile("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:$jacksonVersion")
    compile("com.fasterxml.jackson.module:jackson-module-guice:$jacksonVersion")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-guava:$jacksonVersion")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    runtime("org.slf4j:slf4j-simple:1.7.21")
}
