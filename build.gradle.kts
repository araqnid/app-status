plugins {
    kotlin("jvm") version "1.3.61" apply false
    id("com.timgroup.webpack") version "1.0.63" apply false
    id("com.jfrog.bintray") version "1.8.4" apply false
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")
val versionPrefix = "0.0"

allprojects {
    group = "org.araqnid.app-status"

    if (buildNumber != null)
        version = "${versionPrefix}.${buildNumber}"
}

LibraryVersions.toMap().forEach { (name, value) ->
    ext["${name}Version"] = value
}
