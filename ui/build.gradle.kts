import com.timgroup.gradle.webpack.WebpackTask

plugins {
    id("com.timgroup.webpack")
}

node {
    version = "8.11.1"
    download = true
}

val web by configurations.creating

val webpack by tasks.getting(WebpackTask::class) {
    inputs.file("webpack.config.js")
    inputs.file("yarn.lock")
}

dependencies {
    web(files("$buildDir/site") {
        builtBy(webpack)
    })
}
