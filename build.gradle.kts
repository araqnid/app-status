plugins {
    kotlin("jvm") version "1.6.20" apply false
    id("com.github.node-gradle.node") version("3.0.1") apply false
}

allprojects {
    group = "org.araqnid.app-status"
    version = "0.1.6"
}

subprojects {
    repositories {
        mavenCentral()
    }
}
