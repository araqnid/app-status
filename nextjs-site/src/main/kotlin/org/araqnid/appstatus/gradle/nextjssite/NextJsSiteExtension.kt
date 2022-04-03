package org.araqnid.appstatus.gradle.nextjssite

import org.gradle.api.provider.Property

abstract class NextJsSiteExtension {
    abstract val debugBuild: Property<Boolean>
    abstract val productionProfiling: Property<Boolean>
    abstract val lint: Property<Boolean>

    init {
        @Suppress("LeakingThis")
        debugBuild.convention(false)
        @Suppress("LeakingThis")
        productionProfiling.convention(false)
        @Suppress("LeakingThis")
        lint.convention(true)
    }
}
