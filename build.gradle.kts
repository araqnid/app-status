plugins {
    kotlin("jvm") version "1.5.31" apply false
    id("com.github.node-gradle.node") version("3.0.1") apply false
}

allprojects {
    group = "org.araqnid.app-status"
    version = "0.2.0"
}

subprojects {
    repositories {
        mavenCentral()
    }
}
