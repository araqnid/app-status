plugins {
    kotlin("jvm") version "1.4.30" apply false
    id("com.timgroup.webpack") version "1.0.63" apply false
}

allprojects {
    group = "org.araqnid.app-status"
    version = "0.1.5"
}

subprojects {
    repositories {
        mavenCentral()

        if (isGithubUserAvailable(project)) {
            for (repo in listOf("assert-that")) {
                maven(url = "https://maven.pkg.github.com/araqnid/$repo") {
                    name = "github-$repo"
                    credentials(githubUserCredentials(project))
                }
            }
        }
    }
}
