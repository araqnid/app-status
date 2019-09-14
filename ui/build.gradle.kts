import com.timgroup.gradle.webpack.WebpackTask

plugins {
    id("com.timgroup.webpack")
}

node {
    version = "12.10.0"
    download = true
}

val web by configurations.creating

tasks.named("webpack", WebpackTask::class) {
    inputs.file("webpack.config.js")
    inputs.file("yarn.lock")
}

dependencies {
    web(files("$buildDir/site") {
        builtBy("webpack")
    })
}
