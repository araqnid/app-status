plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
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
    implementation(project(":guice"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-servlet:$jettyVersion")
    implementation(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:1.3.1"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
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
