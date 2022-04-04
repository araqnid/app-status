import com.github.gradle.node.yarn.task.YarnTask
import org.araqnid.appstatus.gradle.nextjssite.NextJsSiteExtension

plugins {
    base
    id("com.github.node-gradle.node")
}

val extension = extensions.create<NextJsSiteExtension>("nextJsSite")

val web by configurations.creating

val siteDir = layout.buildDirectory.dir("site")
val testResultsDir = layout.buildDirectory.dir("test-results")

val nextBuild by tasks.registering(YarnTask::class) {
    description = "Build Next.js server"
    inputs.files(fileTree("src"))
    inputs.files(file("next.config.js"))
    inputs.files(file("package.json"))
    inputs.files(file("yarn.lock"))
    outputs.dir(".next")
    dependsOn("yarn")
    args.set(listOf("next", "build"))
    args.addAll(extension.debugBuild.map { if (it) listOf("--debug") else emptyList() })
    args.addAll(extension.productionProfiling.map { if (it) listOf("--profile") else emptyList() })
    args.addAll(extension.lint.map { if (it) emptyList() else listOf("--no-lint") })
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
