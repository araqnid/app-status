plugins {
    kotlin("jvm") version "1.4.30" apply false
    id("com.timgroup.webpack") version "1.0.63" apply false
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
