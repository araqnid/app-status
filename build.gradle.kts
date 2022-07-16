plugins {
    kotlin("jvm") version "1.7.10" apply false
    kotlin("plugin.serialization") version "1.7.10" apply false
    id("com.github.node-gradle.node") version("3.0.1") apply false
}

allprojects {
    group = "org.araqnid.app-status"
    version = "0.4.0"
}

subprojects {
    repositories {
        mavenCentral()
    }
}
