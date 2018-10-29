import com.timgroup.gradle.webpack.MochaTestTask
import com.timgroup.gradle.webpack.WebpackTask

plugins {
    id("com.timgroup.webpack")
}

node {
    version = "8.12.0"
    download = true
}

val web by configurations.creating

val webpack by tasks.getting(WebpackTask::class) {
    inputs.file("webpack.config.js")
    inputs.file("yarn.lock")
}

val mochaTest by tasks.getting(MochaTestTask::class) {
    inputs.file("mocha-hook.js")
    inputs.file("testSetup.js")
}

dependencies {
    web(files("$buildDir/site") {
        builtBy(webpack)
    })
}
