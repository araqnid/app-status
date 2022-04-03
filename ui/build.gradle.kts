import com.github.gradle.node.yarn.task.YarnTask

plugins {
    base
    id("com.github.node-gradle.node")
}

val web by configurations.creating

val siteDir = layout.buildDirectory.dir("site")

tasks.named("yarn_export").configure {
    dependsOn("yarn_build")
}

val nextBuild by tasks.registering(YarnTask::class) {
    inputs.files(fileTree("pages"))
    inputs.files(fileTree("components"))
    outputs.dir(".next")
    dependsOn("yarn")
    args.set(listOf("next", "build"))
}

val nextExport by tasks.registering(YarnTask::class) {
    inputs.files(fileTree(".next"))
    outputs.dir(siteDir)
    dependsOn("yarn", "nextBuild")
    args.set(siteDir.map { listOf("next", "export", "-o", it.toString()) })
}

tasks.named("assemble").configure {
    dependsOn(nextExport)
}

dependencies {
    web(files(nextExport))
}
