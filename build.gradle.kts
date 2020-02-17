plugins {
    kotlin("jvm") version "1.3.61" apply false
    id("com.timgroup.webpack") version "1.0.63" apply false
    id("com.jfrog.bintray") version "1.8.4" apply false
}

allprojects {
    group = "org.araqnid.app-status"

    if (rootProject.hasProperty("version"))
        version = rootProject.property("version").toString()
}

LibraryVersions.toMap().forEach { (name, value) ->
    ext["${name}Version"] = value
}
