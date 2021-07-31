import com.timgroup.gradle.webpack.WebpackTask

plugins {
    id("com.timgroup.webpack")
}

node {
    version = "14.17.3"
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
