plugins {
    id("com.timgroup.webpack")
}

node {
    version = "8.11.1"
    download = true
}

val web by configurations.creating

dependencies {
    web(files("$buildDir/site") {
        builtBy(tasks["webpack"])
    })
}
