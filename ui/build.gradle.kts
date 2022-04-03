import com.github.gradle.node.yarn.task.YarnTask

plugins {
    base
    id("com.github.node-gradle.node")
}

val web by configurations.creating

val siteDir = layout.buildDirectory.dir("site")
val testResultsDir = layout.buildDirectory.dir("test-results")

val nextBuild by tasks.registering(YarnTask::class) {
    description = "Build Next.js server"
    inputs.files(fileTree("src"))
    outputs.dir(".next")
    dependsOn("yarn")
    args.set(listOf("next", "build"))
}

val nextExport by tasks.registering(YarnTask::class) {
    description = "Export Next.js pages to static files"
    inputs.files(fileTree(".next"))
    outputs.dir(siteDir)
    dependsOn("yarn", "nextBuild")
    args.set(siteDir.map { listOf("next", "export", "-o", it.toString()) })
}

val jestTest by tasks.registering(YarnTask::class) {
    val taskOutputDir = testResultsDir.map { it.dir(name) }
    group = "verification"
    description = "Run Javascript tests using Jest on nodejs"
    inputs.files(fileTree("src"))
    outputs.dir(taskOutputDir)
    dependsOn("yarn")
    args.set(listOf("jest", "--ci", "--reporters=default", "--reporters=jest-junit"))
    environment.put("JEST_JUNIT_OUTPUT_DIR", taskOutputDir.map { it.toString() })
    environment.put("JEST_JUNIT_OUTPUT_NAME", "UI-jest-node.xml")
}

tasks.named("assemble").configure {
    dependsOn(nextExport)
}

tasks.named("check").configure {
    dependsOn(jestTest)
}

dependencies {
    web(files(nextExport))
}
