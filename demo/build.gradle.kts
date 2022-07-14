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
    val slf4jVersion = "1.7.36"
    val jettyVersion = "11.0.9"
    constraints {
        add("implementation", "org.slf4j:slf4j-api") {
            version {
                strictly("[1.7, 1.8[")
                prefer(slf4jVersion)
            }
        }
    }
    implementation(project(":guice"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-servlet:$jettyVersion")
    implementation(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:1.3.3"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
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
